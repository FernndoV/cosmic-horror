package com.vefe.cosmichorror.application;

import com.vefe.cosmichorror.domain.*;
import java.util.*;

public record SessionView(UUID sessionId, UUID campaignId, UUID characterId, String openingNarrative, int version, int sanity, String storyStatus, String endingType, String objective, String objectiveStatus, List<NarrativeObjective> objectives, Set<String> clues, List<Turn> turns) {}