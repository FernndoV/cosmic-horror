package com.vefe.cosmichorror;

import com.vefe.cosmichorror.adapters.out.StubNarrativeGenerationAdapter;
import com.vefe.cosmichorror.domain.Campaign;
import com.vefe.cosmichorror.domain.NarrativeProposal;
import com.vefe.cosmichorror.domain.NarrativeRequest;
import com.vefe.cosmichorror.domain.PlayerCharacter;
import com.vefe.cosmichorror.domain.Turn;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StubNarrativeGenerationAdapterTest {
  private final StubNarrativeGenerationAdapter adapter = new StubNarrativeGenerationAdapter();
  private final Campaign innsmouth = new Campaign(UUID.randomUUID(), "La señal bajo el hielo",
      "Horror marítimo", "1926", "Innsmouth", "Opresivo");
  private final PlayerCharacter mara = new PlayerCharacter(UUID.randomUUID(), innsmouth.id(),
      "Mara", "Cartógrafa de costas imposibles");

  @Test
  void openingUsesCampaignAndCharacterContext() {
    String opening = adapter.generateOpening(innsmouth, mara);

    assertTrue(opening.contains("Innsmouth"));
    assertTrue(opening.contains("1926"));
    assertTrue(opening.contains("Horror marítimo"));
    assertTrue(opening.contains("Opresivo"));
    assertTrue(opening.contains("Mara"));
    assertTrue(opening.contains("Cartógrafa de costas imposibles"));
  }

  @Test
  void differentActionsProduceDifferentCampaignBoundScenes() {
    NarrativeProposal inspect = adapter.generate(request("Examino las marcas", Set.of(), List.of()));
    NarrativeProposal enter = adapter.generate(request("Abro la puerta", Set.of(), List.of()));

    assertNotEquals(inspect.narrative(), enter.narrative());
    assertEquals("impossible_map", inspect.clue());
    assertEquals("impossible_staircase", enter.clue());
    assertTrue(inspect.narrative().contains("Innsmouth"));
    assertTrue(enter.narrative().contains("Horror marítimo"));
  }

  @Test
  void knownClueAndPreviousTurnChangeTheNextResponseWithoutDuplicatingClue() {
    Turn previous = new Turn(UUID.randomUUID(), UUID.randomUUID(), "Examino las marcas",
        "El mapa ya señala una ruta hacia el puerto.", 1);
    NarrativeProposal result = adapter.generate(request("Vuelvo a mirar el mapa",
        Set.of("impossible_map"), List.of(previous)));

    assertNull(result.clue());
    assertTrue(result.narrative().contains("Examino las marcas"));
    assertTrue(result.narrative().contains("ya no es un descubrimiento"));
    assertTrue(result.narrative().contains("Innsmouth"));
  }

  private NarrativeRequest request(String action, Set<String> clues, List<Turn> turns) {
    return new NarrativeRequest(UUID.randomUUID(), action, 10, clues, innsmouth, mara,
        "La señal llega desde el puerto.", turns);
  }
}
