package com.vefe.cosmichorror.application;

import com.vefe.cosmichorror.domain.AdventureSession;
import com.vefe.cosmichorror.domain.PlayerCharacter;
import com.vefe.cosmichorror.domain.InvalidSkillException;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.UUID;

public final class RollSkillCheckUseCase {
  private final SessionRepository sessions;
  private final Optional<CharacterRepository> characters;

  public RollSkillCheckUseCase(SessionRepository sessions, CharacterRepository characters) {
    this.sessions = sessions;
    this.characters = Optional.ofNullable(characters);
  }

  public SkillCheckResult execute(UUID id, String skill) {
    AdventureSession session = sessions.find(id).orElseThrow(SessionNotFoundException::new);
    if (characters.isEmpty() || session.characterId() == null) throw new InvalidSkillException();
    PlayerCharacter character = characters.get().find(session.characterId())
        .orElseThrow(CharacterNotFoundException::new);
    Integer modifier = character.skills().get(skill);
    if (modifier == null) throw new InvalidSkillException();

    int dieOne = ThreadLocalRandom.current().nextInt(1, 7);
    int dieTwo = ThreadLocalRandom.current().nextInt(1, 7);
    int total = dieOne + dieTwo + modifier;
    return new SkillCheckResult(skill, modifier, dieOne, dieTwo, total, 7, total >= 7);
  }
}




