package com.vefe.cosmichorror.application;

import com.vefe.cosmichorror.domain.AdventureSession;
import com.vefe.cosmichorror.domain.Turn;
import java.util.*;

public interface SessionRepository {
  Optional<AdventureSession> find(UUID id);
  void save(AdventureSession session);
  void saveTurn(AdventureSession session, Turn turn, int expectedVersion);
}