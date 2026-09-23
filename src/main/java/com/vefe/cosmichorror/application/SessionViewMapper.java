package com.vefe.cosmichorror.application;

import com.vefe.cosmichorror.domain.AdventureSession;

public final class SessionViewMapper {
  private SessionViewMapper() {}

  public static SessionView toView(AdventureSession session) {
    return new SessionView(session.id(), session.campaignId(), session.characterId(),
        session.openingNarrative(), session.version(), session.sanity(), session.storyStatus(),
        session.endingType(), session.objective(), session.objectiveStatus(), session.objectives(),
        session.clues(), session.turns());
  }
}

