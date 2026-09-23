package com.vefe.cosmichorror.application;

import com.vefe.cosmichorror.domain.Turn;
import java.util.UUID;

public record TurnResult(UUID sessionId, Turn turn, SessionView state) {}
