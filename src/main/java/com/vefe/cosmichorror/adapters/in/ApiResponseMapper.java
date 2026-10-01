package com.vefe.cosmichorror.adapters.in;

import com.vefe.cosmichorror.application.SessionView;
import com.vefe.cosmichorror.domain.Campaign;
import com.vefe.cosmichorror.domain.NarrativeObjective;
import com.vefe.cosmichorror.domain.PlayerCharacter;
import com.vefe.cosmichorror.domain.Turn;
import java.util.List;
import java.util.Set;
import java.util.UUID;

final class ApiResponseMapper {
  private ApiResponseMapper() {}

  static CampaignResponse campaign(Campaign campaign) {
    return new CampaignResponse(campaign.id(), campaign.name(), campaign.setting(), campaign.era(),
        campaign.location(), campaign.tone(), campaign.centralThreat(), campaign.playerObjective());
  }

  static CharacterResponse character(PlayerCharacter character) {
    return new CharacterResponse(character.id(), character.campaignId(), character.name(),
        character.concept(), character.portraitUrl(), character.skills());
  }

  static SessionResponse session(SessionView session) {
    return new SessionResponse(session.sessionId(), session.campaignId(), session.characterId(),
        session.openingNarrative(), session.version(), session.sanity(), session.storyStatus(),
        session.endingType(), session.objective(), session.objectiveStatus(), session.objectives(),
        session.clues(), session.turns());
  }

  static TurnResponse turn(UUID sessionId, Turn turn, SessionView state) {
    return new TurnResponse(sessionId, turn, session(state));
  }

  record CampaignResponse(UUID id, String name, String setting, String era, String location, String tone,
      String centralThreat, String playerObjective) {}

  record CharacterResponse(UUID id, UUID campaignId, String name, String concept,
      String portraitUrl, java.util.Map<String, Integer> skills) {}

  record SessionResponse(UUID sessionId, UUID campaignId, UUID characterId, String openingNarrative,
      int version, int sanity, String storyStatus, String endingType, String objective,
      String objectiveStatus, List<NarrativeObjective> objectives, Set<String> clues,
      List<Turn> turns) {}

  record TurnResponse(UUID sessionId, Turn turn, SessionResponse state) {}
}

