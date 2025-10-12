package com.jwattsuk.enrichment.controller;

import com.jwattsuk.enrichment.model.TradeEnrichmentRequest;
import com.jwattsuk.enrichment.model.TradeEnrichmentResponse;
import com.jwattsuk.enrichment.service.TradeEnrichmentService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/enrichment")
public class EnrichmentController {

    private static final Logger LOG = LoggerFactory.getLogger(EnrichmentController.class);

    private final TradeEnrichmentService enrichmentService;

    public EnrichmentController(final TradeEnrichmentService enrichmentService) {
        this.enrichmentService = enrichmentService;
    }

    @PostMapping("/trade")
    public ResponseEntity<TradeEnrichmentResponse> enrichTrade(
            @Valid @RequestBody final TradeEnrichmentRequest request) {
        LOG.info("Received enrichment request for trade ID: {}", request.getTradeId());

        try {
            var response = enrichmentService.enrichTrade(request);
            return ResponseEntity.ok(response);
        } catch (final Exception e) {
            LOG.error("Error processing enrichment request for trade ID: {}", request.getTradeId(), e);
            return ResponseEntity.internalServerError()
                    .body(createErrorResponse(request.getTradeId(), e.getMessage()));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "enrichment-service",
                "timestamp", LocalDateTime.now()));
    }

    private TradeEnrichmentResponse createErrorResponse(final String tradeId, final String errorMessage) {
        return new TradeEnrichmentResponse(
                tradeId,
                Map.of("error", "Service unavailable"),
                Map.of("error", "Risk calculation failed"),
                Map.of("error", "Pricing unavailable"),
                "ERROR: " + errorMessage,
                LocalDateTime.now(),
                0L);
    }
}