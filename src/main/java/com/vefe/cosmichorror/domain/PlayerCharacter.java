package com.vefe.cosmichorror.domain;

import java.util.*;

public record PlayerCharacter(UUID id, UUID campaignId, String name, String concept, String portraitUrl, Map<String, Integer> skills) {
  public PlayerCharacter(UUID id, UUID campaignId, String name, String concept) {
    this(id, campaignId, name, concept, null, Map.of());
  }
  public PlayerCharacter(UUID id, UUID campaignId, String name, String concept, String portraitUrl) {
    this(id, campaignId, name, concept, portraitUrl, Map.of());
  }
  public PlayerCharacter {
    if (name == null || name.isBlank()) throw new IllegalArgumentException("Character name is required");
    skills = skills == null ? Map.of() : Collections.unmodifiableMap(new LinkedHashMap<>(skills));
  }
}