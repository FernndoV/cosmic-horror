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
    List<Turn> recentTurns,
    NarrativeState state,
    ActionInterpretation actionInterpretation) {
  public NarrativeRequest {
    action = action == null ? "" : action.trim();
    clues = clues == null ? Set.of() : Set.copyOf(clues);
    openingNarrative = openingNarrative == null ? "" : openingNarrative;
    recentTurns = recentTurns == null ? List.of() : List.copyOf(recentTurns);
    state = state == null ? NarrativeState.initial(campaign, null, clues) : state;
    actionInterpretation = actionInterpretation == null
        ? new NarrativeActionInterpreter().interpret(action) : actionInterpretation;
  }

  public NarrativeRequest(UUID sessionId, String action, int sanity, Set<String> clues,
      Campaign campaign, PlayerCharacter character, String openingNarrative, List<Turn> recentTurns) {
    this(sessionId, action, sanity, clues, campaign, character, openingNarrative, recentTurns,
        NarrativeState.initial(campaign, null, clues), new NarrativeActionInterpreter().interpret(action));
  }
  public NarrativeRequest(UUID sessionId, String action, int sanity, Set<String> clues) {
    this(sessionId, action, sanity, clues, null, null, "", List.of(),
        NarrativeState.initial(null, null, clues), new NarrativeActionInterpreter().interpret(action));
  }
}
