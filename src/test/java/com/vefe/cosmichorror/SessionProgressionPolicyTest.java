package com.vefe.cosmichorror;

import com.vefe.cosmichorror.application.SessionProgressionPolicy;
import com.vefe.cosmichorror.domain.NarrativeProposal;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SessionProgressionPolicyTest {
  private final SessionProgressionPolicy policy = new SessionProgressionPolicy();

  @Test
  void closesAfterTheConfiguredTurnLimit() {
    NarrativeProposal proposal = new NarrativeProposal("Resultado", null, 0);

    NarrativeProposal result = policy.apply(proposal, "Observo la puerta", 5);

    assertEquals("CONCLUDED", result.storyStatus());
    assertEquals("AMBIGUOUS", result.endingType());
    assertEquals("RESOLVED", result.objectiveStatus());
  }

  @Test
  void closesWhenThePlayerExplicitlyLeavesAfterTheOpening() {
    NarrativeProposal proposal = new NarrativeProposal("Resultado", null, 0);

    NarrativeProposal result = policy.apply(proposal, "Abandono el lugar", 2);

    assertEquals("CONCLUDED", result.storyStatus());
  }

  @Test
  void preservesAnOpenSessionBeforeTheClosingPhase() {
    NarrativeProposal proposal = new NarrativeProposal("Resultado", null, 0);

    NarrativeProposal result = policy.apply(proposal, "Observo la puerta", 2);

    assertEquals("ONGOING", result.storyStatus());
  }
}

