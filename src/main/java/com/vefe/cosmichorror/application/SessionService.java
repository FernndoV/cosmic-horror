package com.vefe.cosmichorror.application;

import com.vefe.cosmichorror.domain.NarrativeGenerationPort;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class SessionService {
  private final SessionRepository repository;
  private final CreateSessionUseCase createSession;
  private final SubmitTurnUseCase submitTurn;
  private final RollSkillCheckUseCase rollSkillCheck;

  public SessionService(SessionRepository repository, NarrativeGenerationPort narrative) {
    this(repository, narrative, null, null);
  }

  @org.springframework.beans.factory.annotation.Autowired
  public SessionService(SessionRepository repository, NarrativeGenerationPort narrative,
      CampaignRepository campaigns, CharacterRepository characters) {
    this.repository = repository;
    SessionProgressionPolicy progressionPolicy = new SessionProgressionPolicy();
    this.createSession = new CreateSessionUseCase(repository, narrative, campaigns, characters);
    this.submitTurn = new SubmitTurnUseCase(repository, narrative, campaigns, characters, progressionPolicy);
    this.rollSkillCheck = new RollSkillCheckUseCase(repository, characters);
  }

  public SessionView create() {
    return create(null, null);
  }

  public SessionView create(UUID campaignId, UUID characterId) {
    return createSession.execute(campaignId, characterId);
  }

  public SessionView get(UUID id) {
    return repository.find(id)
        .map(SessionViewMapper::toView)
        .orElseThrow(SessionNotFoundException::new);
  }

  public TurnResult submit(UUID id, UUID requestId, int expectedVersion, String action) {
    return submitTurn.execute(id, requestId, expectedVersion, action);
  }

  public SkillCheckResult rollCheck(UUID id, String skill) {
    return rollSkillCheck.execute(id, skill);
  }
}

