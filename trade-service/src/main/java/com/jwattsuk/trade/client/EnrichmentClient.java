package com.jwattsuk.trade.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

@Component
public class EnrichmentClient {

    private static final Logger LOG = LoggerFactory.getLogger(EnrichmentClient.class);

    private final WebClient webClient;

    public EnrichmentClient(final WebClient.Builder webClientBuilder,
                           @Value("${enrichment.service.url:http://localhost:8081}") final String enrichmentServiceUrl,
                           @Value("${enrichment.service.timeout:30}") final int timeoutSeconds) {
        this.webClient = webClientBuilder
            .baseUrl(enrichmentServiceUrl)
            .build();
    }

    public Map<String, Object> enrichTrade(final Map<String, Object> tradeData) {
        LOG.debug("Sending enrichment request for trade: {}", tradeData.get("tradeId"));
        
        try {
            var response = webClient
                .post()
                .uri("/api/enrichment/trade")
                .bodyValue(tradeData)
                .retrieve()
                .bodyToMono(Map.class)
                .timeout(Duration.ofSeconds(30))
                .block();

            LOG.info("Successfully received enrichment response for trade: {}", tradeData.get("tradeId"));
            return response;

        } catch (final WebClientResponseException e) {
            LOG.error("Enrichment service returned error for trade {}: {} - {}", 
                tradeData.get("tradeId"), e.getStatusCode(), e.getMessage());
            return createFailureResponse(String.valueOf(tradeData.get("tradeId")), e.getMessage());
        } catch (final Exception e) {
            LOG.error("Failed to call enrichment service for trade {}: {}", 
                tradeData.get("tradeId"), e.getMessage(), e);
            return createFailureResponse(String.valueOf(tradeData.get("tradeId")), e.getMessage());
        }
    }

    private Map<String, Object> createFailureResponse(final String tradeId, final String errorMessage) {
        return Map.of(
            "tradeId", tradeId,
            "enrichmentStatus", "FAILURE",
            "error", errorMessage,
            "marketData", Map.of("error", "Service unavailable"),
            "riskMetrics", Map.of("error", "Service unavailable"),
            "pricingData", Map.of("error", "Service unavailable")
        );
    }
}