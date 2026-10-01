package com.vefe.cosmichorror.adapters.out;

import com.vefe.cosmichorror.application.CampaignRepository;
import com.vefe.cosmichorror.domain.Campaign;
import java.util.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(name = "persistence.mode", havingValue = "postgres")
public class JdbcCampaignRepository implements CampaignRepository {
  private final JdbcTemplate jdbc;
  public JdbcCampaignRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }
  public Optional<Campaign> find(UUID id) { return jdbc.query("SELECT id,name,setting,era,location,tone,central_threat,player_objective FROM campaign WHERE id=?", this::map, id).stream().findFirst(); }
  public void save(Campaign c) { jdbc.update("INSERT INTO campaign(id,name,setting,era,location,tone,central_threat,player_objective) VALUES (?,?,?,?,?,?,?,?) ON CONFLICT (id) DO UPDATE SET name=EXCLUDED.name,setting=EXCLUDED.setting,era=EXCLUDED.era,location=EXCLUDED.location,tone=EXCLUDED.tone,central_threat=EXCLUDED.central_threat,player_objective=EXCLUDED.player_objective", c.id(), c.name(), c.setting(), c.era(), c.location(), c.tone(), c.centralThreat(), c.playerObjective()); }
  private Campaign map(java.sql.ResultSet rs, int row) throws java.sql.SQLException { return new Campaign(rs.getObject("id", UUID.class), rs.getString("name"), rs.getString("setting"), rs.getString("era"), rs.getString("location"), rs.getString("tone"), rs.getString("central_threat"), rs.getString("player_objective")); }
}
