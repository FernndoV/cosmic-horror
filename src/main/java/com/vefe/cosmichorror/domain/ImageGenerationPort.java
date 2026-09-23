package com.vefe.cosmichorror.domain;

public interface ImageGenerationPort {
  String generatePortrait(Campaign campaign, PlayerCharacter character);
}