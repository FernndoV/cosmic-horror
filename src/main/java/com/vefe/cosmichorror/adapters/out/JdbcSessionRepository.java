package com.vefe.cosmichorror.adapters.out;

import com.vefe.cosmichorror.application.SessionRepository;
import com.vefe.cosmichorror.domain.*;
import java.util.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@ConditionalOnProperty(name = "persistence.mode", havingValue = "postgres")
public class JdbcSessionRepository implements SessionRepository {
  private final JdbcTemplate jdbc;
  public JdbcSessionRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }
  public Optional<AdventureSession> find(UUID id) {
    List<AdventureSession> sessions = jdbc.query("SELECT id,campaign_id,character_id,opening_narrative,version,sanity,story_status,ending_type,objective,objective_status FROM adventure_session WHERE id=?", (rs, row) -> {
      UUID sessionId = rs.getObject("id", UUID.class);
      List<Turn> turns = jdbc.query("SELECT id,request_id,action,narrative,version FROM adventure_turn WHERE session_id=? ORDER BY version", (tr, i) -> new Turn(tr.getObject("id", UUID.class), tr.getObject("request_id", UUID.class), tr.getString("action"), tr.getString("narrative"), tr.getInt("version")), sessionId);
      List<NarrativeObjective> objectives = jdbc.query("SELECT objective_id,objective_text,objective_status FROM session_objective WHERE session_id=? ORDER BY objective_id", (or, i) -> new NarrativeObjective(or.getString("objective_id"), or.getString("objective_text"), or.getString("objective_status")), sessionId);
      Set<String> clues = new LinkedHashSet<>(jdbc.query("SELECT clue FROM session_clue WHERE session_id=?", (cr, i) -> cr.getString("clue"), sessionId));
      return AdventureSession.restore(sessionId, rs.getObject("campaign_id", UUID.class), rs.getObject("character_id", UUID.class), rs.getString("opening_narrative"), rs.getInt("version"), rs.getInt("sanity"), rs.getString("story_status"), rs.getString("ending_type"), rs.getString("objective"), rs.getString("objective_status"), objectives, clues, turns);
    }, id);
    return sessions.stream().findFirst();
  }
  @Transactional public void save(AdventureSession session) {
    jdbc.update("INSERT INTO adventure_session(id,campaign_id,character_id,opening_narrative,version,sanity,story_status,ending_type,objective,objective_status) VALUES (?,?,?,?,?,?,?,?,?,?) ON CONFLICT(id) DO UPDATE SET campaign_id=EXCLUDED.campaign_id,character_id=EXCLUDED.character_id,opening_narrative=EXCLUDED.opening_narrative,version=EXCLUDED.version,sanity=EXCLUDED.sanity,story_status=EXCLUDED.story_status,ending_type=EXCLUDED.ending_type,objective=EXCLUDED.objective,objective_status=EXCLUDED.objective_status", session.id(), session.campaignId(), session.characterId(), session.openingNarrative(), session.version(), session.sanity(), session.storyStatus(), session.endingType(), session.objective(), session.objectiveStatus());
    jdbc.update("DELETE FROM session_objective WHERE session_id=?", session.id());
    session.objectives().forEach(o -> jdbc.update("INSERT INTO session_objective(session_id,objective_id,objective_text,objective_status) VALUES (?,?,?,?)", session.id(), o.id(), o.text(), o.status()));
    jdbc.update("DELETE FROM adventure_turn WHERE session_id=?", session.id());
    session.turns().forEach(t -> jdbc.update("INSERT INTO adventure_turn(id,session_id,request_id,action,narrative,version) VALUES (?,?,?,?,?,?)", t.id(), session.id(), t.requestId(), t.action(), t.narrative(), t.version()));
    jdbc.update("DELETE FROM session_clue WHERE session_id=?", session.id());
    session.clues().forEach(clue -> jdbc.update("INSERT INTO session_clue(session_id,clue) VALUES (?,?)", session.id(), clue));
  }
  @Transactional public void saveTurn(AdventureSession session, Turn turn, int expectedVersion) {
    int updated = jdbc.update("UPDATE adventure_session SET version=?, sanity=?, story_status=?, ending_type=?, objective=?, objective_status=? WHERE id=? AND version=?", session.version(), session.sanity(), session.storyStatus(), session.endingType(), session.objective(), session.objectiveStatus(), session.id(), expectedVersion);
    if (updated != 1) throw new ConcurrentSessionException();
    jdbc.update("INSERT INTO adventure_turn(id,session_id,request_id,action,narrative,version) VALUES (?,?,?,?,?,?)", turn.id(), session.id(), turn.requestId(), turn.action(), turn.narrative(), turn.version());
    jdbc.update("DELETE FROM session_objective WHERE session_id=?", session.id());
    session.objectives().forEach(o -> jdbc.update("INSERT INTO session_objective(session_id,objective_id,objective_text,objective_status) VALUES (?,?,?,?)", session.id(), o.id(), o.text(), o.status()));
    session.clues().forEach(clue -> jdbc.update("INSERT INTO session_clue(session_id,clue) VALUES (?,?) ON CONFLICT DO NOTHING", session.id(), clue));
  }
}