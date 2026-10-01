package com.vefe.cosmichorror.domain;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** Provider-neutral source of truth for the fiction at the start of a turn. */
public record NarrativeState(
    String phase,
    String currentLocation,
    List<NarrativeEntity> presentEntities,
    Set<String> knownFacts,
    Set<String> discoveredClues,
    String activeObjective,
    Set<String> unresolvedConsequences,
    int tensionClock) {

  public NarrativeState {
    phase = phase == null || phase.isBlank() ? "opening" : phase.trim().toLowerCase();
    currentLocation = require(currentLocation, "currentLocation");
    presentEntities = presentEntities == null ? List.of() : List.copyOf(presentEntities);
    knownFacts = immutable(knownFacts);
    discoveredClues = immutable(discoveredClues);
    activeObjective = require(activeObjective, "activeObjective");
    unresolvedConsequences = immutable(unresolvedConsequences);
    if (tensionClock < 0) throw new IllegalArgumentException("tensionClock cannot be negative");
  }

  public static NarrativeState initial(Campaign campaign, String objective, Set<String> clues) {
    String location = campaign == null || campaign.location() == null || campaign.location().isBlank()
        ? "localización no establecida" : campaign.location().trim();
    Set<String> facts = new LinkedHashSet<>();
    facts.add("location:" + location.toLowerCase());
    if (campaign != null) {
      addFact(facts, "campaign:" + campaign.name());
      addFact(facts, "setting:" + campaign.setting());
      addFact(facts, "era:" + campaign.era());
      addFact(facts, "threat:" + campaign.centralThreat());
      addFact(facts, "objective:" + campaign.playerObjective());
    }
    String effectiveObjective = objective;
    if (effectiveObjective == null || effectiveObjective.isBlank()) {
      effectiveObjective = campaign == null ? null : campaign.playerObjective();
    }
    return new NarrativeState("opening", location, List.of(), facts,
        clues == null ? Set.of() : clues, effectiveObjective == null || effectiveObjective.isBlank()
            ? "Comprender la amenaza y decidir qué precio aceptar." : objective, Set.of(), 0);
  }

  public NarrativeState apply(NarrativeTransition transition) {
    new NarrativeTransitionValidator().validate(this, transition);
    Set<String> facts = new LinkedHashSet<>(knownFacts);
    facts.addAll(transition.establishedFacts());
    Set<String> clues = new LinkedHashSet<>(discoveredClues);
    clues.addAll(transition.discoveredClues());
    Set<String> consequences = new LinkedHashSet<>(unresolvedConsequences);
    consequences.removeAll(transition.resolvedConsequences());
    consequences.addAll(transition.establishedFacts().stream()
        .filter(fact -> fact.startsWith("consequence:"))
        .map(fact -> fact.substring("consequence:".length())).toList());
    List<NarrativeEntity> entities = presentEntities.stream()
        .filter(entity -> !transition.removedEntityIds().contains(entity.id())).toList();
    entities = new java.util.ArrayList<>(entities);
    entities.addAll(transition.introducedEntities());
    String nextPhase = transition.toLocation().equals(currentLocation) ? phase : "development";
    return new NarrativeState(nextPhase, transition.toLocation(), entities, facts, clues,
        activeObjective, consequences, Math.max(0, tensionClock + transition.tensionDelta()));
  }

  public boolean containsEntity(String id) {
    return presentEntities.stream().anyMatch(entity -> entity.id().equals(id));
  }

  private static void addFact(Set<String> facts, String value) {
    if (value != null && !value.endsWith(":null") && !value.endsWith(":")) facts.add(value.toLowerCase());
  }

  private static Set<String> immutable(Set<String> values) {
    return values == null ? Set.of() : Set.copyOf(new LinkedHashSet<>(values));
  }

  private static String require(String value, String field) {
    if (value == null || value.isBlank()) throw new IllegalArgumentException(field + " is required");
    return value.trim();
  }
}
