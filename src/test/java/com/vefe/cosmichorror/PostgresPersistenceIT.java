package com.vefe.cosmichorror;

import com.vefe.cosmichorror.application.*;
import com.vefe.cosmichorror.domain.*;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
class PostgresPersistenceIT {
  @Container
  static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");

  @DynamicPropertySource
  static void databaseProperties(DynamicPropertyRegistry properties) {
    properties.add("persistence.mode", () -> "postgres");
    properties.add("spring.datasource.url", POSTGRES::getJdbcUrl);
    properties.add("spring.datasource.username", POSTGRES::getUsername);
    properties.add("spring.datasource.password", POSTGRES::getPassword);
    properties.add("spring.flyway.enabled", () -> "true");
  }

  @Autowired CampaignService campaigns;
  @Autowired CharacterService characters;
  @Autowired SessionService sessions;

  @Test
  void persistsCampaignCharacterSessionAndTurn() {
    Campaign campaign = campaigns.create("La señal bajo el hielo", "Horror cósmico", "1926", "Innsmouth", "Opresivo");
    PlayerCharacter character = characters.create(campaign.id(), "Mara", "Cartógrafa de costas imposibles");
    SessionView created = sessions.create(campaign.id(), character.id());
    UUID requestId = UUID.randomUUID();
    sessions.submit(created.sessionId(), requestId, 0, "Escucho detrás de la puerta");

    SessionView reloaded = sessions.get(created.sessionId());
    assertEquals(campaign.id(), reloaded.campaignId());
    assertEquals(character.id(), reloaded.characterId());
    assertEquals(1, reloaded.version());
    assertEquals(requestId, reloaded.turns().getFirst().requestId());
  }
}
