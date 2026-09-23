package com.vefe.cosmichorror.domain;

import java.util.Set;
import java.util.regex.Pattern;

public final class NarrativeProposalValidator {
  private static final Set<String> STORY_STATUSES = Set.of("ONGOING", "CONCLUDED");
  private static final Set<String> ENDING_TYPES = Set.of("VICTORY", "DEFEAT", "SACRIFICE", "ESCAPE", "AMBIGUOUS");
  private static final Set<String> OBJECTIVE_STATUSES = Set.of("ACTIVE", "RESOLVED", "ABANDONED", "TRANSFORMED");
  private static final Pattern CLUE_ID = Pattern.compile("[a-z0-9][a-z0-9_-]{0,79}");

  public NarrativeProposal validate(NarrativeProposal proposal) {
    if (proposal == null) throw new IllegalStateException("La propuesta narrativa es nula");

    String narrative = proposal.narrative() == null ? "" : proposal.narrative().trim();
    String storyStatus = normalize(proposal.storyStatus(), "ONGOING");
    String objectiveStatus = normalize(proposal.objectiveStatus(), "ACTIVE");
    String endingType = normalizeNullable(proposal.endingType());
    String clue = normalizeClue(proposal.clue());
    int sanityCost = Math.max(0, Math.min(proposal.sanityCost(), 3));

    NarrativeProposal normalized = new NarrativeProposal(narrative, clue, sanityCost, storyStatus,
        endingType, proposal.objective(), objectiveStatus, proposal.objectives());

    if (!normalized.valid() || !STORY_STATUSES.contains(storyStatus)
        || (endingType != null && !ENDING_TYPES.contains(endingType))
        || !OBJECTIVE_STATUSES.contains(objectiveStatus)
        || (clue != null && !CLUE_ID.matcher(clue).matches())) {
      throw new IllegalStateException("La propuesta narrativa no cumple el contrato");
    }
    return normalized;
  }

  private String normalize(String value, String fallback) {
    return value == null || value.isBlank() ? fallback : value.trim().toUpperCase();
  }

  private String normalizeNullable(String value) {
    if (value == null || value.isBlank() || "null".equalsIgnoreCase(value)) return null;
    return value.trim().toUpperCase();
  }

  private String normalizeClue(String value) {
    if (value == null || value.isBlank() || "null".equalsIgnoreCase(value) || "none".equalsIgnoreCase(value)) return null;
    return value.trim().toLowerCase();
  }
}

