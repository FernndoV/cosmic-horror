package com.vefe.cosmichorror.adapters.out;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vefe.cosmichorror.adapters.out.*;
import com.vefe.cosmichorror.domain.PlayerCharacter;
import java.util.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(name = "persistence.mode", havingValue = "postgres")
public class JdbcCharacterRepository implements com.vefe.cosmichorror.application.CharacterRepository {
  private final JdbcTemplate jdbc;
  private final ObjectMapper objectMapper;
  public JdbcCharacterRepository(JdbcTemplate jdbc, ObjectMapper objectMapper) { this.jdbc = jdbc; this.objectMapper = objectMapper; }
  public Optional<PlayerCharacter> find(UUID id) { return jdbc.query("SELECT id,campaign_id,name,concept,portrait_url,skills FROM player_character WHERE id=?", this::map, id).stream().findFirst(); }
  public void save(PlayerCharacter c) { jdbc.update("INSERT INTO player_character(id,campaign_id,name,concept,portrait_url,skills) VALUES (?,?,?,?,?,?::jsonb) ON CONFLICT (id) DO UPDATE SET name=EXCLUDED.name,concept=EXCLUDED.concept,portrait_url=EXCLUDED.portrait_url,skills=EXCLUDED.skills", c.id(), c.campaignId(), c.name(), c.concept(), c.portraitUrl(), toJson(c.skills())); }
  public List<PlayerCharacter> findByCampaign(UUID campaignId) { return jdbc.query("SELECT id,campaign_id,name,concept,portrait_url,skills FROM player_character WHERE campaign_id=? ORDER BY created_at", this::map, campaignId); }
  private PlayerCharacter map(java.sql.ResultSet rs, int row) throws java.sql.SQLException {
    try { return new PlayerCharacter(rs.getObject("id", UUID.class), rs.getObject("campaign_id", UUID.class), rs.getString("name"), rs.getString("concept"), rs.getString("portrait_url"), objectMapper.readValue(rs.getString("skills"), new TypeReference<Map<String, Integer>>() {})); }
    catch (Exception exception) { throw new java.sql.SQLException("Invalid character skills", exception); }
  }
  private String toJson(Map<String, Integer> skills) {
    try { return objectMapper.writeValueAsString(skills); }
    catch (Exception exception) { throw new IllegalStateException("Could not serialize character skills", exception); }
  }
}