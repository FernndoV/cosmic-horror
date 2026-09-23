package com.vefe.cosmichorror.domain;

import java.util.UUID;

public record Turn(UUID id, UUID requestId, String action, String narrative, int version) {}
