package com.vefe.cosmichorror.adapters.in;

import com.vefe.cosmichorror.application.CharacterService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/campaigns/{campaignId}/characters")
@CrossOrigin(origins = "http://localhost:4200")
public class CharacterController {
  private final CharacterService service;

  public CharacterController(CharacterService service) {
    this.service = service;
  }

  @PostMapping
  public ResponseEntity<ApiResponseMapper.CharacterResponse> create(
      @PathVariable UUID campaignId,
      @Valid @RequestBody CreateCharacterRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseMapper.character(
        service.create(campaignId, request.name(), request.concept(), request.portraitUrl())));
  }

  @PostMapping("/portrait-preview")
  public PortraitPreview preview(@PathVariable UUID campaignId,
      @Valid @RequestBody PortraitPreviewRequest request) {
    return new PortraitPreview(service.previewPortrait(campaignId, request.name(), request.concept()).portraitUrl());
  }

  @GetMapping
  public List<ApiResponseMapper.CharacterResponse> list(@PathVariable UUID campaignId) {
    return service.list(campaignId).stream().map(ApiResponseMapper::character).toList();
  }

  public record CreateCharacterRequest(
      @NotBlank @Size(max = 120) String name,
      @Size(max = 500) String concept,
      @Size(max = 12_000_000) String portraitUrl) {}

  public record PortraitPreviewRequest(
      @NotBlank @Size(max = 120) String name,
      @Size(max = 500) String concept) {}

  public record PortraitPreview(String portraitUrl) {}
}

