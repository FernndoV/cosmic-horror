package com.vefe.cosmichorror.application;

import com.vefe.cosmichorror.domain.PlayerCharacter;
import java.util.*;

public interface CharacterRepository { Optional<PlayerCharacter> find(UUID id); void save(PlayerCharacter character); List<PlayerCharacter> findByCampaign(UUID campaignId); }
