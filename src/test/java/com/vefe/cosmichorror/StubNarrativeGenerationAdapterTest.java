package com.vefe.cosmichorror;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.vefe.cosmichorror.adapters.out.InMemoryCampaignRepository;
import com.vefe.cosmichorror.adapters.out.InMemoryCharacterRepository;
import com.vefe.cosmichorror.adapters.out.InMemorySessionRepository;
import com.vefe.cosmichorror.adapters.out.StubNarrativeGenerationAdapter;
import com.vefe.cosmichorror.application.SessionService;
import com.vefe.cosmichorror.application.SessionView;
import com.vefe.cosmichorror.application.TurnResult;
import com.vefe.cosmichorror.domain.Campaign;
import com.vefe.cosmichorror.domain.NarrativeProposal;
import com.vefe.cosmichorror.domain.NarrativeRequest;
import com.vefe.cosmichorror.domain.PlayerCharacter;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class StubNarrativeGenerationAdapterTest {
  private final StubNarrativeGenerationAdapter adapter = new StubNarrativeGenerationAdapter();

  @Test
  void keepsCampaignLocationInOpeningAndTurnAndIsDeterministic() {
    Campaign campaign = new Campaign(UUID.randomUUID(), "La señal bajo el hielo",
        "Horror cósmico", "1926", "Innsmouth", "Opresivo");
    PlayerCharacter character = new PlayerCharacter(UUID.randomUUID(), campaign.id(),
        "Mara", "Cartógrafa de costas imposibles");

    String opening = adapter.generateOpening(campaign, character);
    NarrativeRequest request = new NarrativeRequest(UUID.randomUUID(), "Observo las marcas",
        10, Set.of(), campaign, character, opening, java.util.List.of());

    NarrativeProposal first = adapter.generate(request);
    NarrativeProposal second = adapter.generate(request);

    assertTrue(opening.contains("Innsmouth"));
    assertTrue(first.narrative().contains("Innsmouth"));
    assertEquals(first, second);
  }

  @Test
  void usesCampaignNameAsFallbackWhenLocationIsMissing() {
    Campaign campaign = new Campaign(UUID.randomUUID(), "El faro sumergido",
        "Horror marítimo", "1926", null, "Opresivo");

    NarrativeProposal proposal = adapter.generate(new NarrativeRequest(
        UUID.randomUUID(), "Escucho la señal", 10, Set.of(), campaign, null, "", java.util.List.of()));

    assertTrue(proposal.narrative().contains("El faro sumergido"));
  }

  @Test
  void sessionPreservesCanonicalCampaignContextAndRequestIdempotence() {
    InMemoryCampaignRepository campaigns = new InMemoryCampaignRepository();
    InMemoryCharacterRepository characters = new InMemoryCharacterRepository();
    InMemorySessionRepository sessions = new InMemorySessionRepository();
    SessionService service = new SessionService(sessions, adapter, campaigns, characters);

    Campaign campaign = new Campaign(UUID.randomUUID(), "La señal bajo el hielo",
        "Horror cósmico", "1926", "Innsmouth", "Opresivo");
    campaigns.save(campaign);
    PlayerCharacter character = new PlayerCharacter(UUID.randomUUID(), campaign.id(),
        "Mara", "Cartógrafa");
    characters.save(character);

    SessionView created = service.create(campaign.id(), character.id());
    UUID requestId = UUID.randomUUID();
    TurnResult first = service.submit(created.sessionId(), requestId, 0, "Observo las marcas");
    TurnResult repeated = service.submit(created.sessionId(), requestId, 0, "Acción ignorada por reintento");

    assertTrue(created.openingNarrative().contains("Innsmouth"));
    assertTrue(first.turn().narrative().contains("Innsmouth"));
    assertEquals(first.turn(), repeated.turn());
    assertEquals(1, repeated.state().version());
  }

  @Test
  void staleVersionIsRejectedBeforeGeneratingAnotherNarrative() {
    int[] calls = {0};
    InMemorySessionRepository sessions = new InMemorySessionRepository();
    SessionService service = new SessionService(sessions, request -> {
      calls[0]++;
      return new NarrativeProposal("Resultado", null, 0);
    });

    SessionView created = service.create();
    service.submit(created.sessionId(), UUID.randomUUID(), 0, "Primera acción");

    assertThrows(com.vefe.cosmichorror.domain.ConcurrentSessionException.class,
        () -> service.submit(created.sessionId(), UUID.randomUUID(), 0, "Reintento obsoleto"));
    assertEquals(1, calls[0]);
  }
}
