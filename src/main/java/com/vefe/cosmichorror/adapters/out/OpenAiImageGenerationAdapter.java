package com.vefe.cosmichorror.adapters.out;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vefe.cosmichorror.domain.Campaign;
import com.vefe.cosmichorror.domain.ImageGenerationPort;
import com.vefe.cosmichorror.domain.PlayerCharacter;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@ConditionalOnProperty(name = "ai.provider", havingValue = "openai")
public class OpenAiImageGenerationAdapter implements ImageGenerationPort {
  private static final String STYLE_VERSION = "portrait-style-v1";
  private static final Logger log = LoggerFactory.getLogger(OpenAiImageGenerationAdapter.class);
  private final RestClient client;
  private final ObjectMapper objectMapper;
  private final String model;
  private final String imageSize;
  private final String imageStyle;
  private final String outputFormat;
  private final int outputCompression;
  private final String endpoint;

  public OpenAiImageGenerationAdapter(
      ObjectMapper objectMapper,
      @Value("${ai.openai.api-key}") String apiKey,
      @Value("${ai.openai.base-url}") String baseUrl,
      @Value("${ai.openai.image-model}") String model,
      @Value("${ai.openai.image-size}") String imageSize,
      @Value("${ai.openai.image-style}") String imageStyle,
      @Value("${ai.openai.output-format}") String outputFormat,
      @Value("${ai.openai.output-compression}") int outputCompression) {
    if (apiKey == null || apiKey.isBlank()) throw new IllegalStateException("Falta la clave del proveedor de imágenes");
    this.objectMapper = objectMapper;
    this.model = model;
    this.imageSize = imageSize;
    this.imageStyle = imageStyle;
    this.outputFormat = outputFormat;
    this.outputCompression = outputCompression;
    this.endpoint = (baseUrl.endsWith("/") ? baseUrl : baseUrl + "/") + "images/generations";
    this.client = RestClient.builder()
        .baseUrl(baseUrl.endsWith("/") ? baseUrl : baseUrl + "/")
        .defaultHeader("Authorization", "Bearer " + apiKey)
        .defaultHeader("Content-Type", "application/json")
        .build();
  }

  @Override
  public String generatePortrait(Campaign campaign, PlayerCharacter character) {
String prompt = """
        Retrato cuadrado de tinta, horror cósmico, paleta verde salvia y dorado apagado.
        Estilo fijo: %s.
        Persona: %s. Concepto: %s. Época: %s. Localización: %s.
        El concepto define la identidad y el vestuario; respeta la época y el lugar.
        Sin texto, logos, marcas de agua, gore ni criaturas explícitas.
        """.formatted(imageStyle, character.name(), character.concept(), campaign.era(), campaign.location());    try {
      String rawResponse;
      try {
        rawResponse = client.post().uri(endpoint)
            .body(Map.of("model", model, "prompt", prompt, "size", imageSize, "quality", "low", "output_format", outputFormat, "output_compression", outputCompression))
            .retrieve().body(String.class);
      } catch (RestClientResponseException exception) {
        if (exception.getStatusCode().value() != 400 && exception.getStatusCode().value() != 422) throw exception;
        log.info("Image proxy does not support output compression parameters; retrying compatible request");
        rawResponse = client.post().uri(endpoint)
            .body(Map.of("model", model, "prompt", prompt, "size", imageSize, "quality", "low"))
            .retrieve().body(String.class);
      }
      JsonNode response = objectMapper.readTree(rawResponse);
      JsonNode data = response == null ? null : response.path("data").path(0);
      String url = data == null ? "" : data.path("url").asText("");
      if (!url.isBlank()) return url;
      String base64 = data == null ? "" : data.path("b64_json").asText("");
      return base64.isBlank() ? null : "data:image/" + outputFormat + ";base64," + base64;
    } catch (RestClientResponseException exception) {
      log.warn("Image generation provider returned HTTP {}", exception.getStatusCode().value());
      return null;
    } catch (Exception exception) {
      log.warn("Image generation adapter failed: {}", exception.getClass().getSimpleName());
      return null;
    }
  }
}
