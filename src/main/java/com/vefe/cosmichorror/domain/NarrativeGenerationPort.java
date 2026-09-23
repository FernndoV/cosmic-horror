package com.vefe.cosmichorror.domain;

public interface NarrativeGenerationPort {
  NarrativeProposal generate(NarrativeRequest request);

  default String generateOpening(Campaign campaign, PlayerCharacter character) {
    return "La noche comienza sin dejar rastro, y algo antiguo parece haber reconocido la llegada de " + character.name() + ".";
  }
}
