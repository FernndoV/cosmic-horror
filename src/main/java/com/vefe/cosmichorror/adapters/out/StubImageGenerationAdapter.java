package com.vefe.cosmichorror.adapters.out;

import com.vefe.cosmichorror.domain.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "ai.provider", havingValue = "stub", matchIfMissing = true)
public class StubImageGenerationAdapter implements ImageGenerationPort {
  @Override
  public String generatePortrait(Campaign campaign, PlayerCharacter character) { return null; }
}