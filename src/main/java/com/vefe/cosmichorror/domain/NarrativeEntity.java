package com.vefe.cosmichorror.domain;

import java.util.LinkedHashMap;
import java.util.Map;

/** A bounded entity in the narrative state; it is not a free-form model invention. */
public record NarrativeEntity(
    String id,
    String kind,
    String location,
    String status,
    Map<String, String> properties,
    Map<String, String> relationships) {

  public NarrativeEntity {
    id = require(id, "id");
    kind = require(kind, "kind");
    location = require(location, "location");
    status = require(status, "status");
    properties = immutable(properties);
    relationships = immutable(relationships);
  }

  public boolean valid() {
    return !id.isBlank() && !kind.isBlank() && !location.isBlank() && !status.isBlank();
  }

  private static String require(String value, String field) {
    if (value == null || value.isBlank()) throw new IllegalArgumentException(field + " is required");
    return value.trim();
  }

  private static Map<String, String> immutable(Map<String, String> values) {
    return values == null ? Map.of() : Map.copyOf(new LinkedHashMap<>(values));
  }
}
