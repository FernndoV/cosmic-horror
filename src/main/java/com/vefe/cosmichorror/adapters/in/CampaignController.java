package com.vefe.cosmichorror.adapters.in;

import com.vefe.cosmichorror.application.CampaignService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/campaigns")
@CrossOrigin(origins = "http://localhost:4200")
public class CampaignController {
  private final CampaignService service;

  public CampaignController(CampaignService service) {
    this.service = service;
  }

  @PostMapping
  public ResponseEntity<ApiResponseMapper.CampaignResponse> create(
      @Valid @RequestBody CreateCampaignRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseMapper.campaign(
        service.create(request.name(), request.setting(), request.era(), request.location(), request.tone())));
  }

  @GetMapping("/{id}")
  public ApiResponseMapper.CampaignResponse get(@PathVariable UUID id) {
    return ApiResponseMapper.campaign(service.get(id));
  }

  public record CreateCampaignRequest(
      @NotBlank @Size(max = 120) String name,
      @Size(max = 2000) String setting,
      @Size(max = 120) String era,
      @Size(max = 200) String location,
      @Size(max = 120) String tone) {}
}

