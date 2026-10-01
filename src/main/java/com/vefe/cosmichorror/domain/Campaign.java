package com.vefe.cosmichorror.domain;

import java.util.UUID;

public record Campaign(UUID id, String name, String setting, String era, String location, String tone,
    String centralThreat, String playerObjective) {
  public Campaign(UUID id, String name, String setting, String era, String location, String tone) {
    this(id, name, setting, era, location, tone, "", "");
  }
  public Campaign { if (name == null || name.isBlank()) throw new IllegalArgumentException("Campaign name is required"); }
}
