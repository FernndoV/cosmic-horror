package com.vefe.cosmichorror.domain;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public final class AdventureSession {
  private static final String DEFAULT_OBJECTIVE = "Comprender la amenaza y decidir qué precio aceptar.";
  private final UUID id;
  private final UUID campaignId;
  private final UUID characterId;
  private final String openingNarrative;
  private int version;
  private int sanity;
  private String storyStatus = "ONGOING";
  private String endingType;
  private String objective = DEFAULT_OBJECTIVE;
  private String objectiveStatus = "ACTIVE";
  private NarrativeState narrativeState;
  private final List<NarrativeObjective> objectives = new ArrayList<>();
  private final Set<String> clues = new LinkedHashSet<>();
  private final List<Turn> turns = new ArrayList<>();

  private AdventureSession(UUID id, UUID campaignId, UUID characterId, String openingNarrative,
      NarrativeState narrativeState) {
    this.id = id;
    this.campaignId = campaignId;
    this.characterId = characterId;
    this.openingNarrative = openingNarrative;
    this.narrativeState = narrativeState == null
        ? NarrativeState.initial(null, DEFAULT_OBJECTIVE, Set.of()) : narrativeState;
    this.sanity = 10;
    this.objectives.add(new NarrativeObjective("main", DEFAULT_OBJECTIVE, "ACTIVE"));
  }

  public static AdventureSession start(UUID id) {
    return start(id, null, null, "La noche comienza sin dejar rastro.",
        NarrativeState.initial(null, DEFAULT_OBJECTIVE, Set.of()));
  }

  public static AdventureSession start(UUID id, UUID campaignId, UUID characterId) {
    return start(id, campaignId, characterId, "La noche comienza sin dejar rastro.",
        NarrativeState.initial(null, DEFAULT_OBJECTIVE, Set.of()));
  }

  public static AdventureSession start(UUID id, UUID campaignId, UUID characterId,
      String openingNarrative) {
    return start(id, campaignId, characterId, openingNarrative,
        NarrativeState.initial(null, DEFAULT_OBJECTIVE, Set.of()));
  }

  public static AdventureSession start(UUID id, UUID campaignId, UUID characterId,
      String openingNarrative, NarrativeState narrativeState) {
    return new AdventureSession(id, campaignId, characterId, openingNarrative, narrativeState);
  }

  public static AdventureSession restore(UUID id, UUID campaignId, UUID characterId,
      String openingNarrative, int version, int sanity, Set<String> clues, List<Turn> turns) {
    return restore(id, campaignId, characterId, openingNarrative, version, sanity, "ONGOING",
        null, DEFAULT_OBJECTIVE, "ACTIVE", null, clues, turns, null);
  }

  public static AdventureSession restore(UUID id, UUID campaignId, UUID characterId,
      String openingNarrative, int version, int sanity, String storyStatus, String endingType,
      Set<String> clues, List<Turn> turns) {
    return restore(id, campaignId, characterId, openingNarrative, version, sanity, storyStatus,
        endingType, DEFAULT_OBJECTIVE, "ACTIVE", null, clues, turns, null);
  }

  public static AdventureSession restore(UUID id, UUID campaignId, UUID characterId,
      String openingNarrative, int version, int sanity, String storyStatus, String endingType,
      String objective, String objectiveStatus, List<NarrativeObjective> restoredObjectives,
      Set<String> clues, List<Turn> turns) {
    return restore(id, campaignId, characterId, openingNarrative, version, sanity, storyStatus,
        endingType, objective, objectiveStatus, restoredObjectives, clues, turns, null);
  }

  public static AdventureSession restore(UUID id, UUID campaignId, UUID characterId,
      String openingNarrative, int version, int sanity, String storyStatus, String endingType,
      String objective, String objectiveStatus, List<NarrativeObjective> restoredObjectives,
      Set<String> clues, List<Turn> turns, NarrativeState restoredState) {
    String effectiveObjective = objective == null || objective.isBlank()
        ? DEFAULT_OBJECTIVE : objective;
    NarrativeState state = restoredState == null
        ? NarrativeState.initial(null, effectiveObjective, clues) : restoredState;
    AdventureSession session = new AdventureSession(id, campaignId, characterId, openingNarrative, state);
    session.version = version;
    session.sanity = sanity;
    session.storyStatus = storyStatus == null ? "ONGOING" : storyStatus;
    session.endingType = endingType;
    session.objective = effectiveObjective;
    session.objectiveStatus = objectiveStatus == null ? "ACTIVE" : objectiveStatus;
    if (restoredObjectives != null && !restoredObjectives.isEmpty()) {
      session.objectives.clear();
      session.objectives.addAll(restoredObjectives);
    }
    if (clues != null) session.clues.addAll(clues);
    if (turns != null) session.turns.addAll(turns);
    return session;
  }

  public synchronized Turn apply(UUID requestId, String action, NarrativeProposal proposal,
      int expectedVersion) {
    if (version != expectedVersion) throw new ConcurrentSessionException();
    if (concluded()) throw new StoryAlreadyConcludedException();
    if (!proposal.valid()) throw new InvalidNarrativeProposalException();
    if (proposal.transition() != null) narrativeState = narrativeState.apply(proposal.transition());
    sanity = Math.max(0, sanity - proposal.sanityCost());
    storyStatus = proposal.storyStatus();
    endingType = proposal.endingType();
    if (proposal.objective() != null && !proposal.objective().isBlank()) {
      objective = proposal.objective().trim();
    }
    objectiveStatus = proposal.objectiveStatus();
    if (proposal.objectives() != null && !proposal.objectives().isEmpty()) {
      objectives.clear();
      objectives.addAll(proposal.objectives());
    }
    if (proposal.clue() != null && !proposal.clue().isBlank()
        && !proposal.clue().equalsIgnoreCase("none")
        && !proposal.clue().equalsIgnoreCase("null")) {
      clues.add(proposal.clue());
    }
    Turn turn = new Turn(UUID.randomUUID(), requestId, action, proposal.narrative(), version + 1);
    turns.add(turn);
    version++;
    return turn;
  }

  public UUID id() { return id; }
  public UUID campaignId() { return campaignId; }
  public String openingNarrative() { return openingNarrative; }
  public UUID characterId() { return characterId; }
  public int version() { return version; }
  public int sanity() { return sanity; }
  public String storyStatus() { return storyStatus; }
  public String endingType() { return endingType; }
  public String objective() { return objective; }
  public String objectiveStatus() { return objectiveStatus; }
  public NarrativeState narrativeState() { return narrativeState; }
  public List<NarrativeObjective> objectives() { return List.copyOf(objectives); }
  public boolean concluded() { return "CONCLUDED".equals(storyStatus); }
  public Set<String> clues() { return Set.copyOf(clues); }
  public List<Turn> turns() { return List.copyOf(turns); }
  public Optional<Turn> turnByRequestId(UUID requestId) {
    return turns.stream().filter(t -> t.requestId().equals(requestId)).findFirst();
  }
}
