package com.vefe.cosmichorror.adapters.out;

import com.vefe.cosmichorror.application.SessionRepository;
import com.vefe.cosmichorror.domain.AdventureSession;
import com.vefe.cosmichorror.domain.ConcurrentSessionException;
import com.vefe.cosmichorror.domain.Turn;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "persistence.mode", havingValue = "memory", matchIfMissing = true)
public class InMemorySessionRepository implements SessionRepository {
  private final Map<UUID, AdventureSession> sessions = new ConcurrentHashMap<>();
  public Optional<AdventureSession> find(UUID id) { return Optional.ofNullable(sessions.get(id)); }
  public void save(AdventureSession session) { sessions.put(session.id(), session); }
  public void saveTurn(AdventureSession session, Turn turn, int expectedVersion) {
    AdventureSession current = sessions.get(session.id());
    if (current == null || current.version() != expectedVersion + 1 || session.version() != expectedVersion + 1) {
      throw new ConcurrentSessionException();
    }
    sessions.put(session.id(), session);
  }
}