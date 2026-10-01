package com.vefe.cosmichorror.domain;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Deterministic semantic guardrail. Negation and uncertainty belong to each clause. */
public final class NarrativeActionInterpreter {
  public ActionInterpretation interpret(String rawText) {
    String raw = rawText == null ? "" : rawText.trim();
    List<String> clauses = splitClauses(raw);
    List<ActionClause> actions = clauses.stream().map(this::interpretClause).toList();
    ActionClause primary = actions.stream().filter(action -> action.intent() != ActionIntent.UNKNOWN)
        .findFirst().orElse(new ActionClause(raw, ActionIntent.UNKNOWN, false, false));
    boolean negated = actions.stream().anyMatch(ActionClause::negated);
    boolean uncertain = actions.stream().anyMatch(ActionClause::uncertain);
    return new ActionInterpretation(raw, primary.intent(), negated, uncertain,
        clauses, actions);
  }

  private ActionClause interpretClause(String raw) {
    String normalized = normalize(raw);
    return new ActionClause(raw, detectIntent(normalized), hasNegation(normalized),
        hasUncertainty(normalized));
  }

  private ActionIntent detectIntent(String text) {
    if (contains(text, "digo", "hablo", "pregunto", "respondo", "llamo", "convenzo")) return ActionIntent.SPEAK;
    if (contains(text, "imposible", "atravesar la pared", "volar sin", "respirar bajo el agua")) return ActionIntent.ATTEMPT_IMPOSSIBLE;
    if (contains(text, "esperar", "aguardar", "quedarme quiet", "no hacer nada")) return ActionIntent.WAIT;
    if (contains(text, "huir", "escapar", "retroced", "alejar", "salir", "abandonar")) return ActionIntent.RETREAT;
    if (contains(text, "mover", "ir ", "entrar", "bajar", "subir", "seguir", "cruzar", "abr", "cerrar")) return ActionIntent.MOVE;
    if (contains(text, "usar", "sacar", "colocar", "apuntar", "combinar")) return ActionIntent.USE_OBJECT;
    if (contains(text, "leer", "decifr", "descifr", "estudiar el texto", "examinar el documento")) return ActionIntent.READ;
    if (contains(text, "escuch", "oir", "oír", "sonido", "ruido", "golpe")) return ActionIntent.LISTEN;
    if (contains(text, "investig", "buscar", "registrar", "rastrear")) return ActionIntent.INVESTIGATE;
    if (contains(text, "observ", "mirar", "ver", "examin", "inspeccion", "notar")) return ActionIntent.OBSERVE;
    return ActionIntent.UNKNOWN;
  }

  private boolean hasNegation(String text) {
    return text.matches(".*\\b(no|nunca|jamas|sin|evito|evitar|dejo de|me niego a)\\b.*")
        || text.matches(".*\\b(no quiero|no voy a|no pienso|no intento)\\b.*");
  }

  private boolean hasUncertainty(String text) {
    return text.matches(".*\\b(quiza|quizas|tal vez|acaso|puede que|si puedo|intento|trato de|podria|no se si)\\b.*");
  }

  private boolean contains(String text, String... terms) {
    for (String term : terms) if (text.contains(term)) return true;
    return false;
  }

  private List<String> splitClauses(String raw) {
    if (raw.isBlank()) return List.of();
    List<String> result = new ArrayList<>();
    StringBuilder current = new StringBuilder();
    boolean quoted = false;
    for (int i = 0; i < raw.length(); i++) {
      char character = raw.charAt(i);
      if (character == '"') quoted = !quoted;
      if (!quoted && (character == ',' || character == ';')) {
        addClause(result, current);
        current.setLength(0);
        continue;
      }
      if (!quoted && i + 3 < raw.length() && raw.substring(i, i + 3).matches("\\s[yYeE]\\s")) {
        addClause(result, current);
        current.setLength(0);
        i += 2;
        continue;
      }
      current.append(character);
    }
    addClause(result, current);
    return List.copyOf(result);
  }

  private void addClause(List<String> clauses, StringBuilder value) {
    if (!value.toString().isBlank()) clauses.add(value.toString().trim());
  }

  private String normalize(String value) {
    return Normalizer.normalize(value == null ? "" : value, Normalizer.Form.NFD)
        .replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT).replaceAll("\\s+", " ").trim();
  }
}
