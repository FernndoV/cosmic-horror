package com.vefe.cosmichorror;

import com.vefe.cosmichorror.adapters.in.SessionController.SubmitTurnRequest;
import com.vefe.cosmichorror.application.*;
import com.vefe.cosmichorror.adapters.out.*;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SessionControllerTest {
  @Test void staleVersionIsRejected() {
    SessionRepository repo = new InMemorySessionRepository();
    SessionService service = new SessionService(repo, r -> new com.vefe.cosmichorror.domain.NarrativeProposal("ok", null, 0));
    SessionView created = service.create();
    service.submit(created.sessionId(), UUID.randomUUID(), 0, "Miro");
    assertThrows(com.vefe.cosmichorror.domain.ConcurrentSessionException.class,
        () -> service.submit(created.sessionId(), UUID.randomUUID(), 0, "Escucho"));
  }
  @Test void closureIntentConcludesBeforeTurnSix() {
    SessionService service = new SessionService(new InMemorySessionRepository(), r -> new com.vefe.cosmichorror.domain.NarrativeProposal("ok", null, 0));
    SessionView created = service.create();
    service.submit(created.sessionId(), UUID.randomUUID(), 0, "Observo la puerta");
    service.submit(created.sessionId(), UUID.randomUUID(), 1, "Pregunto a la testigo");
    TurnResult result = service.submit(created.sessionId(), UUID.randomUUID(), 2, "Abandono el lugar y quemo la llave");
    assertEquals("CONCLUDED", result.state().storyStatus());
    assertEquals("AMBIGUOUS", result.state().endingType());
  }
  @Test void repeatedRequestIsIdempotent() {
    SessionRepository repo = new InMemorySessionRepository();
    SessionService service = new SessionService(repo, r -> new com.vefe.cosmichorror.domain.NarrativeProposal("ok", null, 0));
    SessionView created = service.create(); UUID request = UUID.randomUUID();
    TurnResult first = service.submit(created.sessionId(), request, 0, "Miro");
    TurnResult second = service.submit(created.sessionId(), request, 0, "Miro");
    assertEquals(first.turn().id(), second.turn().id()); assertEquals(1, second.state().version());
  }
}
