package com.vefe.cosmichorror.domain;

import java.util.*;

public record NarrativeProposal(String narrative, String clue, int sanityCost, String storyStatus, String endingType, String objective, String objectiveStatus, List<NarrativeObjective> objectives, NarrativeTransition transition) {
  public NarrativeProposal(String narrative, String clue, int sanityCost) { this(narrative, clue, sanityCost, "ONGOING", null, null, "ACTIVE", null, null); }
  public NarrativeProposal(String narrative, String clue, int sanityCost, String storyStatus, String endingType) { this(narrative, clue, sanityCost, storyStatus, endingType, null, "ACTIVE", null, null); }
  public NarrativeProposal(String narrative, String clue, int sanityCost, String storyStatus, String endingType, String objective, String objectiveStatus) { this(narrative, clue, sanityCost, storyStatus, endingType, objective, objectiveStatus, null, null); }
  public NarrativeProposal(String narrative, String clue, int sanityCost, String storyStatus, String endingType, String objective, String objectiveStatus, List<NarrativeObjective> objectives) { this(narrative, clue, sanityCost, storyStatus, endingType, objective, objectiveStatus, objectives, null); }
  public boolean valid() { return narrative != null && !narrative.isBlank() && sanityCost >= 0 && sanityCost <= 10 && ("ONGOING".equals(storyStatus) || "CONCLUDED".equals(storyStatus)) && (!"CONCLUDED".equals(storyStatus) || endingType != null && !endingType.isBlank()) && ("ACTIVE".equals(objectiveStatus) || "RESOLVED".equals(objectiveStatus) || "ABANDONED".equals(objectiveStatus) || "TRANSFORMED".equals(objectiveStatus)) && (objectives == null || objectives.stream().allMatch(NarrativeObjective::valid)) && (transition == null || transition.valid()); }
  public boolean concluded() { return "CONCLUDED".equals(storyStatus); }
}
