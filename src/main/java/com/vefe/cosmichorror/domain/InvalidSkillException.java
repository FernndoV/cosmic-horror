package com.vefe.cosmichorror.domain;

public class InvalidSkillException extends RuntimeException {
  public InvalidSkillException() { super("Unknown skill"); }
}