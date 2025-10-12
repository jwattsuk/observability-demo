package com.jwattsuk.enrichment.service;

import com.jwattsuk.enrichment.model.TradeEnrichmentRequest;
import com.jwattsuk.enrichment.model.TradeEnrichmentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class TradeEnrichmentService {

    private static final Logger LOG = LoggerFactory.getLogger(TradeEnrichmentService.class);

    private final Random random = new Random();

    @Value("${enrichment.delay.min:100}")
    private long minDelayMs;

    @Value("${enrichment.delay.max:2000}")
    private long maxDelayMs;

    @Value("${enrichment.failure.rate:0.05}")
    private double failureRate;

    public TradeEnrichmentResponse enrichTrade(final TradeEnrichmentRequest request) {
        LOG.debug("Starting enrichment for trade ID: {}", request.getTradeId());
        var startTime = System.currentTimeMillis();

        try {
            // Simulate processing delay
            simulateProcessingDelay();

            // Simulate occasional failures
            if (shouldSimulateFailure()) {
                LOG.warn("Simulating enrichment failure for trade ID: {}", request.getTradeId());
                throw new RuntimeException("Simulated enrichment service failure");
            }

            var enrichmentResponse = performEnrichment(request);
            var processingTime = System.currentTimeMillis() - startTime;
            enrichmentResponse.setProcessingTimeMs(processingTime);

            LOG.info("Successfully enriched trade ID: {} in {}ms", request.getTradeId(), processingTime);
            return enrichmentResponse;

        } catch (final Exception e) {
            LOG.error("Failed to enrich trade ID: {}", request.getTradeId(), e);
            return createFailureResponse(request.getTradeId(), System.currentTimeMillis() - startTime);
        }
    }

    private void simulateProcessingDelay() {
        try {
            var delay = minDelayMs + (long) (random.nextDouble() * (maxDelayMs - minDelayMs));
            LOG.debug("Simulating processing delay of {}ms", delay);
            Thread.sleep(delay);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Processing interrupted", e);
        }
    }

    private boolean shouldSimulateFailure() {
        return random.nextDouble() < failureRate;
    }

    private TradeEnrichmentResponse performEnrichment(final TradeEnrichmentRequest request) {
        var marketData = generateMarketData(request);
        var riskMetrics = generateRiskMetrics(request);
        var pricingData = generatePricingData(request);

        return new TradeEnrichmentResponse(
                request.getTradeId(),
                marketData,
                riskMetrics,
                pricingData,
                "SUCCESS",
                LocalDateTime.now(),
                null // Will be set by caller
        );
    }

    private Map<String, Object> generateMarketData(final TradeEnrichmentRequest request) {
        var marketData = new HashMap<String, Object>();

        // Simulate market data based on underlying asset
        switch (request.getUnderlyingAsset().toUpperCase()) {
            case "AAPL" -> {
                marketData.put("currentPrice", generatePrice(150.0, 200.0));
                marketData.put("volatility", generatePercentage(0.15, 0.35));
                marketData.put("sector", "Technology");
            }
            case "EUR/USD" -> {
                marketData.put("currentRate", generatePrice(1.05, 1.15));
                marketData.put("volatility", generatePercentage(0.08, 0.20));
                marketData.put("type", "Currency Pair");
            }
            case "WTI_CRUDE" -> {
                marketData.put("currentPrice", generatePrice(65.0, 85.0));
                marketData.put("volatility", generatePercentage(0.20, 0.45));
                marketData.put("sector", "Energy");
            }
            default -> {
                marketData.put("currentPrice", generatePrice(50.0, 150.0));
                marketData.put("volatility", generatePercentage(0.10, 0.40));
                marketData.put("sector", "Unknown");
            }
        }

        marketData.put("lastUpdated", LocalDateTime.now());
        marketData.put("dataProvider", "Mock Market Data Service");

        return marketData;
    }

    private Map<String, Object> generateRiskMetrics(final TradeEnrichmentRequest request) {
        var riskMetrics = new HashMap<String, Object>();

        var notionalUsd = convertToUsd(request.getNotionalAmount(), request.getCurrency());

        riskMetrics.put("var95", notionalUsd.multiply(generateDecimal(0.01, 0.05)));
        riskMetrics.put("expectedShortfall", notionalUsd.multiply(generateDecimal(0.015, 0.08)));
        riskMetrics.put("creditRisk", generateRiskRating());
        riskMetrics.put("marketRisk", generateRiskLevel());
        riskMetrics.put("liquidityRisk", generateRiskLevel());
        riskMetrics.put("concentrationRisk", generatePercentage(0.05, 0.25));

        return riskMetrics;
    }

    private Map<String, Object> generatePricingData(final TradeEnrichmentRequest request) {
        var pricingData = new HashMap<String, Object>();

        var notional = request.getNotionalAmount();

        pricingData.put("markToMarket", notional.multiply(generateDecimal(0.95, 1.05)));
        pricingData.put("pnl", notional.multiply(generateDecimal(-0.02, 0.03)));
        pricingData.put("delta", generateDecimal(-1.0, 1.0));
        pricingData.put("gamma", generateDecimal(0.0, 0.1));
        pricingData.put("theta", generateDecimal(-0.05, 0.0));
        pricingData.put("vega", generateDecimal(0.0, 0.3));
        pricingData.put("pricingModel", determinePricingModel(request.getInstrumentType()));

        return pricingData;
    }

    private BigDecimal generatePrice(final double min, final double max) {
        var value = min + (random.nextDouble() * (max - min));
        return BigDecimal.valueOf(value).setScale(4, RoundingMode.HALF_UP);
    }

    private BigDecimal generatePercentage(final double min, final double max) {
        var value = min + (random.nextDouble() * (max - min));
        return BigDecimal.valueOf(value).setScale(4, RoundingMode.HALF_UP);
    }

    private BigDecimal generateDecimal(final double min, final double max) {
        var value = min + (random.nextDouble() * (max - min));
        return BigDecimal.valueOf(value).setScale(6, RoundingMode.HALF_UP);
    }

    private BigDecimal convertToUsd(final BigDecimal amount, final String currency) {
        // Simplified currency conversion - in real system would call FX service
        return switch (currency.toUpperCase()) {
            case "EUR" -> amount.multiply(BigDecimal.valueOf(1.10));
            case "GBP" -> amount.multiply(BigDecimal.valueOf(1.25));
            case "JPY" -> amount.divide(BigDecimal.valueOf(150), 2, RoundingMode.HALF_UP);
            default -> amount; // Assume USD
        };
    }

    private String generateRiskRating() {
        var ratings = new String[] { "AAA", "AA", "A", "BBB", "BB", "B", "CCC" };
        return ratings[random.nextInt(ratings.length)];
    }

    private String generateRiskLevel() {
        var levels = new String[] { "LOW", "MEDIUM", "HIGH" };
        return levels[random.nextInt(levels.length)];
    }

    private String determinePricingModel(final String instrumentType) {
        return switch (instrumentType.toUpperCase()) {
            case "OPTION" -> "Black-Scholes";
            case "SWAP" -> "Hull-White";
            case "FUTURE" -> "Cost of Carry";
            default -> "Generic";
        };
    }

    private TradeEnrichmentResponse createFailureResponse(final String tradeId, final long processingTime) {
        return new TradeEnrichmentResponse(
                tradeId,
                Map.of("error", "Failed to retrieve market data"),
                Map.of("error", "Risk calculation failed"),
                Map.of("error", "Pricing unavailable"),
                "FAILURE",
                LocalDateTime.now(),
                processingTime);
    }
}