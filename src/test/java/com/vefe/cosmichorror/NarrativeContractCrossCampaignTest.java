package com.vefe.cosmichorror;

import com.vefe.cosmichorror.adapters.out.StubNarrativeGenerationAdapter;
import com.vefe.cosmichorror.domain.Campaign;
import com.vefe.cosmichorror.domain.NarrativeActionInterpreter;
import com.vefe.cosmichorror.domain.NarrativeProposal;
import com.vefe.cosmichorror.domain.NarrativeProposalValidator;
import com.vefe.cosmichorror.domain.NarrativeRequest;
import com.vefe.cosmichorror.domain.NarrativeState;
import com.vefe.cosmichorror.domain.NarrativeTransition;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NarrativeContractCrossCampaignTest {
  private final StubNarrativeGenerationAdapter adapter = new StubNarrativeGenerationAdapter();

  @Test
  void sameContractWorksForPortObservatoryAndPolarExpedition() {
    Campaign innsmouth = campaign("Puerto de Innsmouth", "Horror marítimo", "Innsmouth");
    Campaign observatory = campaign("La cúpula ciega", "Astronomía anómala", "Observatorio del Cerro");
    Campaign polar = campaign("Noche blanca", "Expedición polar", "Estación Boreal");

    NarrativeProposal port = generate(innsmouth, "Examino las marcas");
    NarrativeProposal dome = generate(observatory, "Observo el telescopio");
    NarrativeProposal ice = generate(polar, "Escucho el viento");

    assertTrue(port.narrative().contains("Innsmouth"));
    assertTrue(dome.narrative().contains("Observatorio del Cerro"));
    assertTrue(ice.narrative().contains("Estación Boreal"));
    assertFalse(dome.narrative().contains("Innsmouth"));
    assertFalse(ice.narrative().contains("Observatorio del Cerro"));
    assertTrue(port.transition().valid());
    assertTrue(dome.transition().valid());
    assertTrue(ice.transition().valid());
  }

  @Test
  void negatedActionDoesNotExecuteTheAffirmativeBranch() {
    Campaign campaign = campaign("Cúpula", "Astronomía", "Observatorio");
    NarrativeRequest request = new NarrativeRequest(UUID.randomUUID(), "No abro la puerta", 10, Set.of(),
        campaign, null, "La puerta ya está establecida.", List.of());

    NarrativeProposal result = adapter.generate(request);

    assertTrue(request.actionInterpretation().negated());
    assertNull(result.clue());
    assertFalse(result.narrative().contains("cede"));
    assertTrue(result.narrative().contains("No ejecutas"));
  }

  @Test
  void repeatedTurnsDoNotRepeatCampaignPreamble() {
    Campaign campaign = campaign("Cúpula", "Astronomía", "Observatorio");
    NarrativeRequest first = new NarrativeRequest(UUID.randomUUID(), "Observo el telescopio", 10, Set.of(),
        campaign, null, "Apertura", List.of());
    NarrativeProposal firstResult = adapter.generate(first);
    NarrativeRequest next = new NarrativeRequest(UUID.randomUUID(), "Vuelvo a observar", 10,
        Set.of("impossible_map"), campaign, null, "Apertura",
        List.of(new com.vefe.cosmichorror.domain.Turn(UUID.randomUUID(), UUID.randomUUID(),
            "Observo el telescopio", firstResult.narrative(), 1)));

    NarrativeProposal nextResult = adapter.generate(next);

    assertTrue(nextResult.narrative().contains("La acción anterior"));
    assertFalse(nextResult.narrative().startsWith("En Observatorio"));
  }

  @Test
  void transitionCannotInventAnUnestablishedLocation() {
    NarrativeState state = NarrativeState.initial(campaign("Base", "Expedición", "Hielo"), null, Set.of());
    NarrativeTransition invalid = new NarrativeTransition(state.currentLocation(), "Ciudad inventada",
        Set.of(), Set.of(), Set.of(), List.of(), Set.of(), 0, "sin causa de localización");

    assertThrows(IllegalStateException.class, () ->
        new NarrativeProposalValidator().validate(new NarrativeProposal(
            "La escena cambia.", null, 0, "ONGOING", null, null, "ACTIVE", null, invalid), state));
  }

  private NarrativeProposal generate(Campaign campaign, String action) {
    return adapter.generate(new NarrativeRequest(UUID.randomUUID(), action, 10, Set.of(),
        campaign, null, "Apertura", List.of()));
  }

  private Campaign campaign(String name, String setting, String location) {
    return new Campaign(UUID.randomUUID(), name, setting, "1931", location, "Opresivo");
  }
}
