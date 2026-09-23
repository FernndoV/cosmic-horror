package com.vefe.cosmichorror.application;

import com.vefe.cosmichorror.domain.NarrativeProposal;
import java.text.Normalizer;
import java.util.Locale;

/** Applies deterministic progression rules around an AI-generated narrative. */
public final class SessionProgressionPolicy {
  private static final String FORCED_CONCLUSION = "Con esta decisión, el conflicto llega a su límite y la amenaza pierde el acceso que buscaba. El personaje abandona el lugar con la respuesta que pudo salvar; no queda ninguna decisión pendiente, solo las consecuencias de lo ocurrido.";

  public NarrativeProposal apply(NarrativeProposal proposal, String action, int version) {
    if (proposal == null || proposal.concluded() || (version < 5 && !isClosureIntent(action, version))) return proposal;
    return new NarrativeProposal(proposal.narrative() + "\n\n" + FORCED_CONCLUSION, proposal.clue(), proposal.sanityCost(), "CONCLUDED", "AMBIGUOUS", null, "RESOLVED", null);
  }

  private boolean isClosureIntent(String action, int version) {
    if (version < 2 || action == null) return false;
    String normalized = Normalizer.normalize(action, Normalizer.Form.NFD).replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT);
    return normalized.matches(".*\\b(huir|huye|huyo|escapar|escapo|escapa|abandonar|abandono|cerrar definitivamente|terminar la investigacion|destruir|destruyo|quemar|quemo)\\b.*");
  }
}

