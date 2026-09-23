package com.vefe.cosmichorror.adapters.out;

import com.vefe.cosmichorror.application.CharacterRepository;
import com.vefe.cosmichorror.domain.PlayerCharacter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "persistence.mode", havingValue = "memory", matchIfMissing = true)
public class InMemoryCharacterRepository implements CharacterRepository {
  private final Map<UUID, PlayerCharacter> characters = new ConcurrentHashMap<>();
  public Optional<PlayerCharacter> find(UUID id) { return Optional.ofNullable(characters.get(id)); }
  public void save(PlayerCharacter character) { characters.put(character.id(), character); }
  public List<PlayerCharacter> findByCampaign(UUID campaignId) { return characters.values().stream().filter(c -> c.campaignId().equals(campaignId)).toList(); }
}
