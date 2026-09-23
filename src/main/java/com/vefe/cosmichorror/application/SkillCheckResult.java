package com.vefe.cosmichorror.application;

public record SkillCheckResult(String skill, int modifier, int dieOne, int dieTwo, int total, int threshold, boolean success) {}