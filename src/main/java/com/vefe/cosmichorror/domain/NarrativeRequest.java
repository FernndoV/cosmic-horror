package com.vefe.cosmichorror.domain;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public record NarrativeRequest(
    UUID sessionId,
    String action,
    int sanity,
    Set<String> clues,
    Campaign campaign,
    PlayerCharacter character,
    String openingNarrative,
    List<Turn> recentTurns) {
  public NarrativeRequest(UUID sessionId, String action, int sanity, Set<String> clues) {
    this(sessionId, action, sanity, clues, null, null, "", List.of());
  }
}