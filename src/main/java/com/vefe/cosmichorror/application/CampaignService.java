package com.vefe.cosmichorror.application;

import com.vefe.cosmichorror.domain.*;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class CampaignService {
  private final CampaignRepository repository;
  public CampaignService(CampaignRepository repository) { this.repository = repository; }
  public Campaign create(String name, String setting, String era, String location, String tone) {
    return create(name, setting, era, location, tone, "", "");
  }
  public Campaign create(String name, String setting, String era, String location, String tone,
      String centralThreat, String playerObjective) {
    Campaign campaign = new Campaign(UUID.randomUUID(), name, setting, era, location, tone,
        centralThreat, playerObjective);
    repository.save(campaign);
    return campaign;
  }
  public Campaign get(UUID id) { return repository.find(id).orElseThrow(CampaignNotFoundException::new); }
}
