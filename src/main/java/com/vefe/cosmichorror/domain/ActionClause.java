package com.vefe.cosmichorror.domain;

public record ActionClause(String rawText, ActionIntent intent, boolean negated, boolean uncertain) {
  public ActionClause {
    rawText = rawText == null ? "" : rawText.trim();
    intent = intent == null ? ActionIntent.UNKNOWN : intent;
  }
}
