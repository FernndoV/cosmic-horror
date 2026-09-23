package com.vefe.cosmichorror.adapters.in;

import com.vefe.cosmichorror.application.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sessions")
@CrossOrigin(origins = "http://localhost:4200")
public class SessionController {
  private final SessionService service;

  public SessionController(SessionService service) {
    this.service = service;
  }

  @PostMapping
  public ResponseEntity<ApiResponseMapper.SessionResponse> create(
      @RequestBody(required = false) CreateSessionRequest request) {
    UUID campaignId = request == null ? null : request.campaignId();
    UUID characterId = request == null ? null : request.characterId();
    return ResponseEntity.status(HttpStatus.CREATED).body(
        ApiResponseMapper.session(service.create(campaignId, characterId)));
  }

  @GetMapping("/{id}")
  public ApiResponseMapper.SessionResponse get(@PathVariable UUID id) {
    return ApiResponseMapper.session(service.get(id));
  }

  @PostMapping("/{id}/checks")
  public SkillCheckResult rollCheck(@PathVariable UUID id,
      @Valid @RequestBody SkillCheckRequest request) {
    return service.rollCheck(id, request.skill());
  }

  @PostMapping("/{id}/turns")
  public ApiResponseMapper.TurnResponse submit(@PathVariable UUID id,
      @Valid @RequestBody SubmitTurnRequest request) {
    TurnResult result = service.submit(id, request.requestId(), request.expectedVersion(), request.action());
    return ApiResponseMapper.turn(result.sessionId(), result.turn(), result.state());
  }

  public record CreateSessionRequest(UUID campaignId, UUID characterId) {}

  public record SkillCheckRequest(@NotBlank @Size(max = 40) String skill) {}

  public record SubmitTurnRequest(
      @NotNull UUID requestId,
      @Min(0) int expectedVersion,
      @NotBlank @Size(max = 2000) String action) {}
}

