package com.vefe.cosmichorror.adapters.in;

import com.vefe.cosmichorror.application.*;
import com.vefe.cosmichorror.domain.*;
import dev.langchain4j.exception.LangChain4jException;
import java.util.Map;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class ApiExceptionHandler {
  @ExceptionHandler(SessionNotFoundException.class)
  ResponseEntity<?> sessionNotFound() { return error(HttpStatus.NOT_FOUND, "SESSION_NOT_FOUND"); }

  @ExceptionHandler(CampaignNotFoundException.class)
  ResponseEntity<?> campaignNotFound() { return error(HttpStatus.NOT_FOUND, "CAMPAIGN_NOT_FOUND"); }

  @ExceptionHandler(CharacterNotFoundException.class)
  ResponseEntity<?> characterNotFound() { return error(HttpStatus.NOT_FOUND, "CHARACTER_NOT_FOUND"); }

  @ExceptionHandler(InvalidSkillException.class)
  ResponseEntity<?> invalidSkill() { return error(HttpStatus.BAD_REQUEST, "INVALID_SKILL"); }

  @ExceptionHandler(InvalidSessionConfigurationException.class)
  ResponseEntity<?> invalidSession() { return error(HttpStatus.UNPROCESSABLE_ENTITY, "INVALID_SESSION_CONFIGURATION"); }

  @ExceptionHandler(ConcurrentSessionException.class)
  ResponseEntity<?> conflict() { return error(HttpStatus.CONFLICT, "SESSION_VERSION_CONFLICT"); }

  @ExceptionHandler(InvalidNarrativeProposalException.class)
  ResponseEntity<?> invalidProposal() { return error(HttpStatus.UNPROCESSABLE_ENTITY, "INVALID_NARRATIVE_PROPOSAL"); }

  @ExceptionHandler(LangChain4jException.class)
  ResponseEntity<?> langChainFailure() { return error(HttpStatus.SERVICE_UNAVAILABLE, "NARRATIVE_PROVIDER_UNAVAILABLE"); }

  @ExceptionHandler(IllegalStateException.class)
  ResponseEntity<?> providerFailure() { return error(HttpStatus.SERVICE_UNAVAILABLE, "NARRATIVE_PROVIDER_UNAVAILABLE"); }

  private ResponseEntity<Map<String, String>> error(HttpStatus status, String code) {
    return ResponseEntity.status(status).body(Map.of("code", code));
  }
}
