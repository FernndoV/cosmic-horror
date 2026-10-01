package com.vefe.cosmichorror.adapters.out;

import com.vefe.cosmichorror.domain.ActionIntent;
import com.vefe.cosmichorror.domain.ActionInterpretation;
import com.vefe.cosmichorror.domain.Campaign;
import com.vefe.cosmichorror.domain.NarrativeActionInterpreter;
import com.vefe.cosmichorror.domain.NarrativeGenerationPort;
import com.vefe.cosmichorror.domain.NarrativeProposal;
import com.vefe.cosmichorror.domain.NarrativeRequest;
import com.vefe.cosmichorror.domain.NarrativeState;
import com.vefe.cosmichorror.domain.NarrativeTransition;
import com.vefe.cosmichorror.domain.PlayerCharacter;
import com.vefe.cosmichorror.domain.Turn;
import java.text.Normalizer;
import java.util.Locale;
import java.util.Set;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/** Deterministic provider that follows the same state and action contract as the model provider. */
@Component
@ConditionalOnProperty(name = "ai.provider", havingValue = "stub", matchIfMissing = true)
public class StubNarrativeGenerationAdapter implements NarrativeGenerationPort {
  private final NarrativeActionInterpreter actionInterpreter = new NarrativeActionInterpreter();

  @Override
  public NarrativeProposal generate(NarrativeRequest request) {
    ActionInterpretation interpretation = request.actionInterpretation() == null
        ? actionInterpreter.interpret(request.action()) : request.actionInterpretation();
    NarrativeState state = request.state();

    com.vefe.cosmichorror.domain.ActionClause executable = interpretation.actions().stream()
        .filter(action -> !action.negated() && action.intent() != ActionIntent.UNKNOWN)
        .findFirst().orElse(null);
    if (executable == null && interpretation.negated()) {
      return proposal(anchor(request) + " No ejecutas «" + shorten(request.action(), 70)
          + "». La negativa conserva el estado: solo queda claro qué posibilidad has decidido no probar."
          + " Puedes cambiar de idea o actuar sobre una señal ya presente.", null, 0,
          NarrativeTransition.noop(state.currentLocation(), "El jugador niega la acción: " + request.action()));
    }
    if (request.recentTurns().size() >= 5) {
      return new NarrativeProposal(anchor(request) + " La decisión final se impone: la amenaza pierde el acceso que buscaba y el precio de haberla comprendido queda en la memoria. Al abandonar la escena, el lugar conserva las huellas de lo ocurrido.", null, 1, "CONCLUDED", "ESCAPE", null, "RESOLVED", null,
          NarrativeTransition.noop(state.currentLocation(), "La cadena de consecuencias alcanza su límite"));
    }

    String base = anchor(request) + continuity(request);
    ActionIntent intent = executable == null ? interpretation.primaryIntent() : executable.intent();
    return switch (intent) {
      case OBSERVE, INVESTIGATE, READ -> inspect(request, base);
      case MOVE -> move(request, base);
      case LISTEN, SPEAK -> listenOrSpeak(request, base);
      case RETREAT -> proposal(base + " Retrocedes sin que el entorno cambie de lugar. La ruta que ya conoces sigue disponible, pero la consecuencia de tu decisión queda abierta y exige escoger entre insistir o conservar distancia.", null, 0,
          NarrativeTransition.noop(state.currentLocation(), request.action()));
      case USE_OBJECT -> proposal(base + " Intentas usar solo lo que la escena y tu acción han establecido. Nada nuevo aparece para resolverlo por ti; el intento revela el límite del recurso y deja una alternativa concreta dentro del espacio que ya conoces.", null, 0,
          NarrativeTransition.noop(state.currentLocation(), request.action()));
      case ATTEMPT_IMPOSSIBLE -> proposal(base + " El intento no puede producir el efecto que describes. La resistencia es observable y no cambia la localización ni crea un objeto nuevo; puedes reformular la acción o atender a una consecuencia que ya estaba presente.", null, 0,
          NarrativeTransition.noop(state.currentLocation(), request.action()));
      case WAIT -> proposal(base + " Esperas. El silencio no se convierte en una escena nueva: un detalle ya presente cambia de ritmo y te permite decidir si sigues observando o intervienes.", null, 0,
          NarrativeTransition.noop(state.currentLocation(), request.action()));
      case UNKNOWN -> proposal(base + " La acción no encuentra todavía un efecto definido. El mundo conserva sus límites y la consecuencia más cercana permanece visible; puedes precisar qué observas, qué intentas cambiar o a quién diriges la palabra.", null, 0,
          NarrativeTransition.noop(state.currentLocation(), request.action()));
    };
  }

  @Override
  public String generateOpening(Campaign campaign, PlayerCharacter character) {
    return "En " + value(campaign == null ? null : campaign.location(), "una localización no establecida")
        + ", durante " + value(campaign == null ? null : campaign.era(), "una época incierta")
        + ", el tono " + value(campaign == null ? null : campaign.tone(), "inquietante")
        + " de " + value(campaign == null ? null : campaign.setting(), "la campaña")
        + " se vuelve concreto. " + value(character == null ? null : character.name(), "El investigador")
        + " (" + value(character == null ? null : character.concept(), "sin concepto definido") + ")"
        + " llega con una pregunta que exige actuar; la señal ya presente permite observarla, seguirla o proteger la salida.";
  }

  private NarrativeProposal inspect(NarrativeRequest request, String base) {
    boolean known = hasClue(request, "impossible_map");
    String narrative = known
        ? base + " La pista del mapa vuelve a alinearse con lo que tienes delante; ya no es un descubrimiento, sino una elección verificable dentro de " + request.state().currentLocation() + ". Puedes seguir la ruta conocida o dejarla marcada para volver."
        : base + " La observación revela un patrón concreto en la superficie que has señalado: sus trazos forman una ruta imposible sin nombrar un destino nuevo. La pista queda disponible para estudiarla o ignorarla.";
    return proposal(narrative, known ? null : "impossible_map", 1,
        new NarrativeTransition(request.state().currentLocation(), request.state().currentLocation(),
            Set.of("fact:the observed pattern is repeatable"), known ? Set.of() : Set.of("impossible_map"), Set.of(), java.util.List.of(), Set.of(), 1, request.action()));
  }

  private NarrativeProposal move(NarrativeRequest request, String base) {
    String action = normalize(request.action());
    if (action.contains("abr") || action.contains("entr") || action.contains("puerta") || action.contains("bajar")) {
      return proposal(base + " La abertura que describes cede lo suficiente para mostrar una continuidad oscura al otro lado. No aparece una localización nueva por sí sola: el umbral es la consecuencia y puedes cruzarlo, cerrarlo o examinarlo.", "impossible_staircase", 1,
          NarrativeTransition.noop(request.state().currentLocation(), request.action()));
    }
    return proposal(base + " Tu movimiento modifica solo la distancia respecto de la señal ya establecida. No se inventa un destino: la ruta termina donde la escena permite comprobarla y puedes continuar o regresar.", null, 0,
        NarrativeTransition.noop(request.state().currentLocation(), request.action()));
  }

  private NarrativeProposal listenOrSpeak(NarrativeRequest request, String base) {
    boolean known = hasClue(request, "three_knocks");
    String narrative = known
        ? base + " Los tres golpes conocidos cambian de ritmo y responden a tu voz sin explicar su origen. La respuesta confirma una relación, no una nueva presencia: puedes contestar o guardar silencio."
        : base + " El sonido que tu acción busca se vuelve distinguible en el espacio inmediato: tres golpes repiten el ritmo que acabas de producir. La pista es concreta y puedes responder o escuchar sin intervenir.";
    return proposal(narrative, known ? null : "three_knocks", 1,
        new NarrativeTransition(request.state().currentLocation(), request.state().currentLocation(),
            Set.of("fact:the response follows the player's rhythm"), known ? Set.of() : Set.of("three_knocks"), Set.of(), java.util.List.of(), Set.of(), 1, request.action()));
  }

  private NarrativeProposal proposal(String narrative, String clue, int sanityCost, NarrativeTransition transition) {
    return new NarrativeProposal(narrative, clue, sanityCost, "ONGOING", null, null, "ACTIVE", null, transition);
  }

  private String anchor(NarrativeRequest request) {
    if (!request.recentTurns().isEmpty()) return "";
    Campaign campaign = request.campaign();
    if (campaign == null) return "La escena empieza en la localización establecida.";
    return "En " + value(campaign.location(), "la localización establecida") + ", " + value(campaign.tone(), "el tono") + " hace perceptible la ambientación de " + value(campaign.setting(), "la campaña") + ".";
  }

  private String continuity(NarrativeRequest request) {
    if (request.recentTurns().isEmpty()) return " ";
    Turn last = request.recentTurns().get(request.recentTurns().size() - 1);
    return " La acción anterior («" + shorten(last.action(), 90) + "») dejó una consecuencia observable: " + shorten(last.narrative(), 130) + ".";
  }

  private boolean hasClue(NarrativeRequest request, String clue) {
    return request.state().discoveredClues().stream().anyMatch(value -> clue.equalsIgnoreCase(value))
        || request.clues().stream().anyMatch(value -> clue.equalsIgnoreCase(value));
  }

  private String normalize(String value) {
    return Normalizer.normalize(value == null ? "" : value, Normalizer.Form.NFD)
        .replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT);
  }

  private String shorten(String value, int maxLength) {
    String compact = value(value, "sin detalles").replaceAll("\\s+", " ").trim();
    return compact.length() <= maxLength ? compact : compact.substring(0, maxLength - 1) + "…";
  }

  private String value(String value, String fallback) {
    return value == null || value.isBlank() ? fallback : value.trim();
  }
}
