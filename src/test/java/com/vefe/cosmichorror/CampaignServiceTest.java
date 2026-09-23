package com.vefe.cosmichorror;

import com.vefe.cosmichorror.adapters.out.*;
import com.vefe.cosmichorror.application.*;
import com.vefe.cosmichorror.domain.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CampaignServiceTest {
  @Test void characterBelongsToExistingCampaign() {
    InMemoryCampaignRepository repository = new InMemoryCampaignRepository();
    CampaignService campaigns = new CampaignService(repository);
    Campaign campaign = campaigns.create("La casa", "Horror", "1920", "Arkham", "Opresivo");
    CharacterService characters = new CharacterService(new InMemoryCharacterRepository(), repository);
    PlayerCharacter character = characters.create(campaign.id(), "Evelyn", "Investigadora");
    assertEquals(campaign.id(), character.campaignId());
  }
}
