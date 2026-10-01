package com.vefe.cosmichorror.domain;

import java.util.List;
import java.util.Set;

/** Explicit, auditable change proposed by a narrative provider. */
public record NarrativeTransition(
    String fromLocation,
    String toLocation,
    Set<String> establishedFacts,
    Set<String> discoveredClues,
    Set<String> resolvedConsequences,
    List<NarrativeEntity> introducedEntities,
    Set<String> removedEntityIds,
    int tensionDelta,
    String cause) {

  public NarrativeTransition {
    establishedFacts = establishedFacts == null ? Set.of() : Set.copyOf(establishedFacts);
    discoveredClues = discoveredClues == null ? Set.of() : Set.copyOf(discoveredClues);
    resolvedConsequences = resolvedConsequences == null ? Set.of() : Set.copyOf(resolvedConsequences);
    introducedEntities = introducedEntities == null ? List.of() : List.copyOf(introducedEntities);
    removedEntityIds = removedEntityIds == null ? Set.of() : Set.copyOf(removedEntityIds);
  }

  public static NarrativeTransition noop(String location, String cause) {
    return new NarrativeTransition(location, location, Set.of(), Set.of(), Set.of(), List.of(), Set.of(), 0, cause);
  }

  public boolean valid() {
    return fromLocation != null && !fromLocation.isBlank()
        && toLocation != null && !toLocation.isBlank()
        && tensionDelta >= -3 && tensionDelta <= 3
        && cause != null && !cause.isBlank()
        && introducedEntities.stream().allMatch(NarrativeEntity::valid);
  }
}
