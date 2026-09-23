package com.vefe.cosmichorror;

import com.vefe.cosmichorror.domain.*;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AdventureSessionTest {
  @Test void appliesValidatedProposalAndChangesState() {
    AdventureSession session = AdventureSession.start(UUID.randomUUID());
    Turn turn = session.apply(UUID.randomUUID(), "Examino las marcas", new NarrativeProposal("Narración", "map", 2), 0);
    assertEquals(1, session.version()); assertEquals(8, session.sanity()); assertTrue(session.clues().contains("map")); assertEquals(1, turn.version());
  }
  @Test void rejectsStaleVersion() {
    AdventureSession session = AdventureSession.start(UUID.randomUUID());
    assertThrows(ConcurrentSessionException.class, () -> session.apply(UUID.randomUUID(), "acción", new NarrativeProposal("ok", null, 0), 1));
  }
}
