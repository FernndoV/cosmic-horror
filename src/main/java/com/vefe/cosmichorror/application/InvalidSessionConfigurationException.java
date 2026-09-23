package com.vefe.cosmichorror.application;

public class InvalidSessionConfigurationException extends RuntimeException {
  public InvalidSessionConfigurationException() { super("Campaign and character must belong to the same session"); }
}
