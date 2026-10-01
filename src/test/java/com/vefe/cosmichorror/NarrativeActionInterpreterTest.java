package com.vefe.cosmichorror;

import com.vefe.cosmichorror.domain.ActionIntent;
import com.vefe.cosmichorror.domain.NarrativeActionInterpreter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NarrativeActionInterpreterTest {
  private final NarrativeActionInterpreter interpreter = new NarrativeActionInterpreter();

  @Test
  void keepsNegationUncertaintyAndMultipleClauses() {
    var result = interpreter.interpret("No abro la puerta y quizá escucho antes de entrar");

    assertEquals(ActionIntent.MOVE, result.primaryIntent());
    assertTrue(result.negated());
    assertTrue(result.uncertain());
    assertEquals(2, result.clauses().size());
  }
}
