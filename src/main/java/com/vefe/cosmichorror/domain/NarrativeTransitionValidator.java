package com.vefe.cosmichorror.domain;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/** Rejects state changes that have no established location, cause, or entity identity. */
public final class NarrativeTransitionValidator {
  private static final Pattern ID = Pattern.compile("[a-z0-9][a-z0-9_-]{0,79}");

  public NarrativeTransition validate(NarrativeState state, NarrativeTransition transition) {
    if (state == null || transition == null || !transition.valid()) {
      throw new IllegalStateException("La transición narrativa no cumple el contrato");
    }
    if (!state.currentLocation().equals(transition.fromLocation().trim())) {
      throw new IllegalStateException("La transición no parte de la localización actual");
    }
    boolean sameLocation = state.currentLocation().equals(transition.toLocation().trim());
    if (!sameLocation && !hasLocationFact(transition.establishedFacts(), transition.toLocation())) {
      throw new IllegalStateException("La nueva localización no está establecida por la transición");
    }
    if (transition.discoveredClues().stream().anyMatch(clue -> !ID.matcher(clue).matches())) {
      throw new IllegalStateException("La transición contiene una pista inválida");
    }
    if (!state.discoveredClues().isEmpty()
        && transition.discoveredClues().stream().anyMatch(state.discoveredClues()::contains)) {
      throw new IllegalStateException("La transición duplica una pista conocida");
    }
    Set<String> ids = new HashSet<>();
    for (NarrativeEntity entity : transition.introducedEntities()) {
      if (!ID.matcher(entity.id()).matches() || !ids.add(entity.id())
          || state.containsEntity(entity.id())
          || !transition.toLocation().equals(entity.location())) {
        throw new IllegalStateException("La transición contiene una entidad ambigua");
      }
    }
    if (transition.removedEntityIds().stream().anyMatch(id -> !state.containsEntity(id))) {
      throw new IllegalStateException("La transición elimina una entidad ausente");
    }
    return transition;
  }

  private boolean hasLocationFact(Set<String> facts, String location) {
    String expected = "location:" + location.trim().toLowerCase();
    return facts.stream().map(String::toLowerCase).anyMatch(expected::equals);
  }
}
