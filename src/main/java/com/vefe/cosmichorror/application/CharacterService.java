package com.vefe.cosmichorror.application;

import com.vefe.cosmichorror.domain.*;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CharacterService {
  private final CharacterRepository repository;
  private final CampaignRepository campaigns;
  private final ImageGenerationPort images;

  public CharacterService(CharacterRepository repository, CampaignRepository campaigns) {
    this(repository, campaigns, (campaign, character) -> null);
  }

  @Autowired
  public CharacterService(CharacterRepository repository, CampaignRepository campaigns, ImageGenerationPort images) {
    this.repository = repository;
    this.campaigns = campaigns;
    this.images = images;
  }

  public PlayerCharacter previewPortrait(UUID campaignId, String name, String concept) {
    Campaign campaign = campaigns.find(campaignId).orElseThrow(CampaignNotFoundException::new);
    return new PlayerCharacter(UUID.randomUUID(), campaignId, name, concept, images.generatePortrait(campaign, new PlayerCharacter(UUID.randomUUID(), campaignId, name, concept)));
  }

  public PlayerCharacter create(UUID campaignId, String name, String concept) {
    return create(campaignId, name, concept, null);
  }

  public PlayerCharacter create(UUID campaignId, String name, String concept, String previewPortraitUrl) {
    Campaign campaign = campaigns.find(campaignId).orElseThrow(CampaignNotFoundException::new);
    PlayerCharacter draft = new PlayerCharacter(UUID.randomUUID(), campaignId, name, concept, null, randomSkills());
    String portraitUrl = usablePreview(previewPortraitUrl) ? previewPortraitUrl : images.generatePortrait(campaign, draft);
    PlayerCharacter character = new PlayerCharacter(draft.id(), draft.campaignId(), draft.name(), draft.concept(), portraitUrl, draft.skills());
    repository.save(character);
    return character;
  }

  private boolean usablePreview(String value) {
    return value != null && (value.startsWith("data:image/") || value.startsWith("https://")) && value.length() <= 12_000_000;
  }

  private Map<String, Integer> randomSkills() {
    List<String> names = List.of("investigacion", "percepcion", "voluntad", "fuerza", "destreza", "persuasion");
    Map<String, Integer> skills = new LinkedHashMap<>();
    Random random = new Random();
    names.forEach(name -> skills.put(name, random.nextInt(5)));
    return skills;
  }

  public List<PlayerCharacter> list(UUID campaignId) { campaigns.find(campaignId).orElseThrow(CampaignNotFoundException::new); return repository.findByCampaign(campaignId); }
  public PlayerCharacter get(UUID id) { return repository.find(id).orElseThrow(CharacterNotFoundException::new); }
}