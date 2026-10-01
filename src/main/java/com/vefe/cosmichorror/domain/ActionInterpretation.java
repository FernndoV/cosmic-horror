package com.vefe.cosmichorror.domain;

import java.util.List;

/** Semantic reading shared by providers, preserving scope per action clause. */
public record ActionInterpretation(
    String rawText,
    ActionIntent primaryIntent,
    boolean negated,
    boolean uncertain,
    List<String> clauses,
    List<ActionClause> actions) {

  public ActionInterpretation(String rawText, ActionIntent primaryIntent, boolean negated,
      boolean uncertain, List<String> clauses) {
    this(rawText, primaryIntent, negated, uncertain, clauses,
        clauses == null ? List.of() : clauses.stream()
            .map(clause -> new ActionClause(clause, primaryIntent, negated, uncertain)).toList());
  }

  public ActionInterpretation {
    rawText = rawText == null ? "" : rawText.trim();
    primaryIntent = primaryIntent == null ? ActionIntent.UNKNOWN : primaryIntent;
    clauses = clauses == null ? List.of() : List.copyOf(clauses);
    actions = actions == null ? List.of() : List.copyOf(actions);
  }

  public boolean isExecutable() {
    return actions.stream().anyMatch(action -> !action.negated() && action.intent() != ActionIntent.UNKNOWN);
  }
}
