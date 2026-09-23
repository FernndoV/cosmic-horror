package com.vefe.cosmichorror.adapters.out;

import com.vefe.cosmichorror.domain.Campaign;
import com.vefe.cosmichorror.domain.NarrativeGenerationPort;
import com.vefe.cosmichorror.domain.NarrativeProposal;
import com.vefe.cosmichorror.domain.NarrativeRequest;
import com.vefe.cosmichorror.domain.PlayerCharacter;
import com.vefe.cosmichorror.domain.Turn;
import java.text.Normalizer;
import java.util.Locale;
import java.util.Set;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/** Deterministic local provider that still consumes the complete narrative state. */
@Component
@ConditionalOnProperty(name = "ai.provider", havingValue = "stub", matchIfMissing = true)
public class StubNarrativeGenerationAdapter implements NarrativeGenerationPort {
  @Override
  public NarrativeProposal generate(NarrativeRequest request) {
    String action = normalize(request.action());
    String context = context(request);

    if (request.recentTurns() != null && request.recentTurns().size() >= 5) {
      return new NarrativeProposal(context + " La decisión final se impone: las marcas pierden su forma, "
          + "la presencia se retira y el precio de haberla comprendido queda en la memoria. "
          + "Al amanecer, el lugar conserva las huellas de lo ocurrido, pero ya no reclama nada de ti.",
          null, 1, "CONCLUDED", "ESCAPE");
    }

    if (containsAny(action, "exam", "mir", "observar", "investig", "marca", "mapa")) {
      boolean mapKnown = hasClue(request, "impossible_map");
      String narrative = context + (mapKnown
          ? " El mapa de la costa imposible vuelve a alinearse con la pared; esta vez señala una ruta que "
              + "termina en " + location(request) + ". La pista ya no es un descubrimiento, sino una elección: seguirla o marcarla para volver."
          : " Al acercarte, las marcas de la pared se reordenan bajo tus dedos y forman un mapa de una costa que no existe. "
              + "Su primer trazo apunta fuera de la habitación, hacia una ruta que aún puedes seguir o abandonar.");
      return proposal(narrative, mapKnown ? null : "impossible_map", 1);
    }

    if (containsAny(action, "abr", "entr", "puerta", "bajar", "sótano", "sotano")) {
      return proposal(context + " La puerta cede con un suspiro húmedo. Bajo el umbral, una escalera desciende "
          + "mucho más de lo que permite el edificio; la salida sigue a tu espalda y la oscuridad espera debajo.",
          "impossible_staircase", 2);
    }

    if (containsAny(action, "escuch", "oír", "oir", "habl", "llam", "responder")) {
      boolean knocksKnown = hasClue(request, "three_knocks");
      return proposal(context + (knocksKnown
          ? " Los tres golpes conocidos cambian de ritmo y repiten la última palabra que pronunciaste. "
              + "La pared ofrece una respuesta, no una explicación: puedes contestar o alejarte antes de que aprenda tu voz."
          : " El sonido se detiene. Después, tres golpes responden desde dentro de la pared, imitando exactamente tu ritmo cardíaco. "
              + "Algo espera a que contestes, aunque también puedes guardar silencio y buscar otra salida."),
          knocksKnown ? null : "three_knocks", 1);
    }

    if (containsAny(action, "huir", "corre", "salir", "alej", "escap")) {
      return proposal(context + " Retrocedes hacia la salida, pero el pasillo parece haberse alargado. "
          + "La casa no quiere que le des la espalda; aun así, la ruta hacia " + location(request)
          + " permanece visible y puedes insistir o detenerte para entender el cambio.", null, 1);
    }

    if (containsAny(action, "invent", "objeto", "llave", "linterna", "sacar", "usar")) {
      return proposal(context + " Entre tus pertenencias encuentras algo que no recuerdas haber guardado: una llave fría, "
          + "cubierta de sal negra. No encaja con la puerta visible; su forma coincide con la marca más reciente y puedes probarla o conservarla.",
          "salt_key", 0);
    }

    return proposal(context + " Tu acción altera el silencio. Algo responde desde un lugar que no aparece en ningún plano; "
        + "la consecuencia de tu movimiento anterior sigue abierta y debes decidir si la persigues o cambias de rumbo.", null, 0);
  }

  @Override
  public String generateOpening(Campaign campaign, PlayerCharacter character) {
    return "En " + value(campaign == null ? null : campaign.location(), "un lugar sin nombre")
        + ", durante " + value(campaign == null ? null : campaign.era(), "una época incierta")
        + ", " + value(campaign == null ? null : campaign.tone(), "el silencio") + " envuelve "
        + value(campaign == null ? null : campaign.name(), "la campaña") + ". "
        + value(campaign == null ? null : campaign.setting(), "El mundo conocido parece estable")
        + ". " + value(character == null ? null : character.name(), "El investigador")
        + " (" + value(character == null ? null : character.concept(), "sin concepto definido")
        + ") llega con una pregunta concreta: algo en este lugar espera una respuesta."
        + " La primera decisión es seguir la señal o proteger la salida.";
  }

  private NarrativeProposal proposal(String narrative, String clue, int sanityCost) {
    return new NarrativeProposal(narrative, clue, sanityCost);
  }

  private String context(NarrativeRequest request) {
    Campaign campaign = request.campaign();
    PlayerCharacter character = request.character();
    String world = campaign == null
        ? "La escena transcurre en un lugar sin campaña definida."
        : "En " + value(campaign.location(), "un lugar no precisado") + ", durante "
            + value(campaign.era(), "una época no precisada") + ", el tono "
            + value(campaign.tone(), "inquietante") + " de " + value(campaign.name(), "la campaña")
            + " se filtra por " + value(campaign.setting(), "el entorno") + ".";
    String actor = character == null ? "" : " " + value(character.name(), "El personaje")
        + ", " + value(character.concept(), "sin concepto definido") + ", nota el cambio. ";
    return world + actor + continuity(request);
  }

  private String continuity(NarrativeRequest request) {
    if (request.recentTurns() == null || request.recentTurns().isEmpty()) {
      return " Aún no hay consecuencias anteriores que interpretar.";
    }
    Turn last = request.recentTurns().get(request.recentTurns().size() - 1);
    return " La consecuencia de tu acción anterior (\"" + shorten(last.action(), 90)
        + "\") sigue presente: " + shorten(last.narrative(), 150) + " ";
  }

  private boolean hasClue(NarrativeRequest request, String clue) {
    Set<String> clues = request.clues();
    return clues != null && clues.stream().anyMatch(value -> clue.equalsIgnoreCase(value));
  }

  private String location(NarrativeRequest request) {
    return value(request.campaign() == null ? null : request.campaign().location(), "la salida");
  }

  private String normalize(String value) {
    return Normalizer.normalize(value(value, ""), Normalizer.Form.NFD)
        .replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT);
  }

  private boolean containsAny(String value, String... terms) {
    for (String term : terms) if (value.contains(normalize(term))) return true;
    return false;
  }

  private String shorten(String value, int maxLength) {
    String compact = value(value, "sin detalles").replaceAll("\\s+", " ").trim();
    return compact.length() <= maxLength ? compact : compact.substring(0, maxLength - 1) + "…";
  }

  private String value(String value, String fallback) {
    return value == null || value.isBlank() ? fallback : value.trim();
  }
}
