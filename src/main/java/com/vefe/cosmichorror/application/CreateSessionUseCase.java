package com.vefe.cosmichorror.application;

import com.vefe.cosmichorror.domain.AdventureSession;
import com.vefe.cosmichorror.domain.Campaign;
import com.vefe.cosmichorror.domain.NarrativeGenerationPort;
import com.vefe.cosmichorror.domain.PlayerCharacter;
import java.util.Optional;
import java.util.UUID;

public final class CreateSessionUseCase {
  private static final String DEFAULT_OPENING = "La noche comienza sin dejar rastro.";

  private final SessionRepository repository;
  private final NarrativeGenerationPort narrative;
  private final Optional<CampaignRepository> campaigns;
  private final Optional<CharacterRepository> characters;

  public CreateSessionUseCase(SessionRepository repository, NarrativeGenerationPort narrative,
      CampaignRepository campaigns, CharacterRepository characters) {
    this.repository = repository;
    this.narrative = narrative;
    this.campaigns = Optional.ofNullable(campaigns);
    this.characters = Optional.ofNullable(characters);
  }

  public SessionView execute(UUID campaignId, UUID characterId) {
    if ((campaignId == null) != (characterId == null)) {
      throw new InvalidSessionConfigurationException();
    }

    String opening = DEFAULT_OPENING;
    if (campaignId != null) {
      CampaignRepository campaignRepository = campaigns.orElseThrow(InvalidSessionConfigurationException::new);
      CharacterRepository characterRepository = characters.orElseThrow(InvalidSessionConfigurationException::new);
      Campaign campaign = campaignRepository.find(campaignId).orElseThrow(CampaignNotFoundException::new);
      PlayerCharacter character = characterRepository.find(characterId)
          .orElseThrow(CharacterNotFoundException::new);
      if (!campaign.id().equals(character.campaignId())) {
        throw new InvalidSessionConfigurationException();
      }
      opening = narrative.generateOpening(campaign, character);
    }

    AdventureSession session = AdventureSession.start(UUID.randomUUID(), campaignId, characterId, opening);
    repository.save(session);
    return SessionViewMapper.toView(session);
  }
}



