package com.vefe.cosmichorror.adapters.out;

import com.vefe.cosmichorror.application.CampaignRepository;
import com.vefe.cosmichorror.domain.Campaign;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "persistence.mode", havingValue = "memory", matchIfMissing = true)
public class InMemoryCampaignRepository implements CampaignRepository {
  private final Map<UUID, Campaign> campaigns = new ConcurrentHashMap<>();
  public Optional<Campaign> find(UUID id) { return Optional.ofNullable(campaigns.get(id)); }
  public void save(Campaign campaign) { campaigns.put(campaign.id(), campaign); }
}
