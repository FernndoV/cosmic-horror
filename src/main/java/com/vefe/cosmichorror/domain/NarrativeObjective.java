package com.vefe.cosmichorror.domain;

public record NarrativeObjective(String id, String text, String status) {
  public boolean valid() { return id != null && !id.isBlank() && text != null && !text.isBlank() && ("ACTIVE".equals(status) || "RESOLVED".equals(status) || "ABANDONED".equals(status) || "TRANSFORMED".equals(status)); }
}