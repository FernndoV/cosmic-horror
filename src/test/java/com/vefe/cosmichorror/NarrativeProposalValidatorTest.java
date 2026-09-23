package com.vefe.cosmichorror;

import com.vefe.cosmichorror.domain.NarrativeProposal;
import com.vefe.cosmichorror.domain.NarrativeProposalValidator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NarrativeProposalValidatorTest {
  private final NarrativeProposalValidator validator = new NarrativeProposalValidator();

  @Test
  void normalizesProviderValuesAndCapsSanityCost() {
    NarrativeProposal result = validator.validate(new NarrativeProposal(
        "  La puerta tiembla.  ", "Three_Knocks", 8, "ongoing", null,
        null, "active", null));

    assertEquals("La puerta tiembla.", result.narrative());
    assertEquals("three_knocks", result.clue());
    assertEquals(3, result.sanityCost());
  }

  @Test
  void rejectsUnknownEndingTypes() {
    NarrativeProposal proposal = new NarrativeProposal(
        "Resultado", null, 1, "CONCLUDED", "SECRET_ENDING");

    assertThrows(IllegalStateException.class, () -> validator.validate(proposal));
  }
}

