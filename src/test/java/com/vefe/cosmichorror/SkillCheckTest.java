package com.vefe.cosmichorror;

import com.vefe.cosmichorror.adapters.out.InMemoryCharacterRepository;
import com.vefe.cosmichorror.adapters.out.InMemoryCampaignRepository;
import com.vefe.cosmichorror.adapters.out.InMemorySessionRepository;
import com.vefe.cosmichorror.application.*;
import com.vefe.cosmichorror.domain.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SkillCheckTest {
  @Test void rollsTwoD6AddsSkillAndUsesSevenAsThreshold() {
    UUID campaignId = UUID.randomUUID();
    UUID characterId = UUID.randomUUID();
    InMemoryCharacterRepository characters = new InMemoryCharacterRepository();
    characters.save(new PlayerCharacter(characterId, campaignId, "Ada", "Exploradora", null, Map.of("investigacion", 4)));
    Campaign campaign = new Campaign(campaignId, "Campaña", "Bosque", "Años 20", "Arkham", "Tenso");
    CampaignRepository campaigns = new InMemoryCampaignRepository();
    campaigns.save(campaign);
    SessionService service = new SessionService(new InMemorySessionRepository(), r -> new NarrativeProposal("Apertura", null, 0), campaigns, characters);
    SessionView session = service.create(campaignId, characterId);
    SkillCheckResult result = service.rollCheck(session.sessionId(), "investigacion");
    assertTrue(result.dieOne() >= 1 && result.dieOne() <= 6);
    assertTrue(result.dieTwo() >= 1 && result.dieTwo() <= 6);
    assertEquals(result.dieOne() + result.dieTwo() + 4, result.total());
    assertEquals(result.total() >= 7, result.success());
    assertEquals(7, result.threshold());
  }
}