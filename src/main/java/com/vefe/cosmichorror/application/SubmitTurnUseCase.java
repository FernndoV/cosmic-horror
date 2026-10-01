package com.vefe.cosmichorror.application;

import com.vefe.cosmichorror.domain.AdventureSession;
import com.vefe.cosmichorror.domain.Campaign;
import com.vefe.cosmichorror.domain.NarrativeGenerationPort;
import com.vefe.cosmichorror.domain.NarrativeProposal;
import com.vefe.cosmichorror.domain.NarrativeProposalValidator;
import com.vefe.cosmichorror.domain.NarrativeState;
import com.vefe.cosmichorror.domain.PlayerCharacter;
import com.vefe.cosmichorror.domain.Turn;
import java.util.Optional;
import java.util.UUID;

public final class SubmitTurnUseCase {
  private final SessionRepository repository;
  private final NarrativeGenerationPort narrative;
  private final Optional<CampaignRepository> campaigns;
  private final Optional<CharacterRepository> characters;
  private final SessionProgressionPolicy progressionPolicy;
  private final NarrativeProposalValidator proposalValidator = new NarrativeProposalValidator();

  public SubmitTurnUseCase(SessionRepository repository, NarrativeGenerationPort narrative,
      CampaignRepository campaigns, CharacterRepository characters,
      SessionProgressionPolicy progressionPolicy) {
    this.repository = repository;
    this.narrative = narrative;
    this.campaigns = Optional.ofNullable(campaigns);
    this.characters = Optional.ofNullable(characters);
    this.progressionPolicy = progressionPolicy;
  }

  public synchronized TurnResult execute(UUID id, UUID requestId, int expectedVersion, String action) {
    AdventureSession session = repository.find(id).orElseThrow(SessionNotFoundException::new);
    Optional<Turn> existing = session.turnByRequestId(requestId);
    if (existing.isPresent()) {
      return new TurnResult(session.id(), existing.get(), SessionViewMapper.toView(session));
    }
    if (session.version() != expectedVersion) {
      throw new com.vefe.cosmichorror.domain.ConcurrentSessionException();
    }

    Campaign campaign = session.campaignId() == null ? null
        : campaigns.flatMap(repositoryPort -> repositoryPort.find(session.campaignId())).orElse(null);
    PlayerCharacter character = session.characterId() == null ? null
        : characters.flatMap(repositoryPort -> repositoryPort.find(session.characterId())).orElse(null);

    NarrativeState state = NarrativeState.initial(campaign, session.objective(), session.clues());
    NarrativeProposal proposal = narrative.generate(new com.vefe.cosmichorror.domain.NarrativeRequest(
        id, action, session.sanity(), session.clues(), campaign, character,
        session.openingNarrative(), session.turns(), state, null));
    proposal = progressionPolicy.apply(proposal, action, session.version());
    proposal = proposalValidator.validate(proposal, state);

    int previousVersion = session.version();
    Turn turn = session.apply(requestId, action, proposal, expectedVersion);
    repository.saveTurn(session, turn, previousVersion);
    return new TurnResult(id, turn, SessionViewMapper.toView(session));
  }
}

