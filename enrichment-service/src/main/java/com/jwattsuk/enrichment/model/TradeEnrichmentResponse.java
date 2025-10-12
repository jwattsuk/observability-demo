package com.jwattsuk.enrichment.model;

import java.time.LocalDateTime;
import java.util.Map;

public class TradeEnrichmentResponse {

    private String tradeId;
    private Map<String, Object> marketData;
    private Map<String, Object> riskMetrics;
    private Map<String, Object> pricingData;
    private String enrichmentStatus;
    private LocalDateTime enrichmentTimestamp;
    private Long processingTimeMs;

    public TradeEnrichmentResponse() {
    }

    public TradeEnrichmentResponse(final String tradeId, final Map<String, Object> marketData,
            final Map<String, Object> riskMetrics, final Map<String, Object> pricingData,
            final String enrichmentStatus, final LocalDateTime enrichmentTimestamp,
            final Long processingTimeMs) {
        this.tradeId = tradeId;
        this.marketData = marketData;
        this.riskMetrics = riskMetrics;
        this.pricingData = pricingData;
        this.enrichmentStatus = enrichmentStatus;
        this.enrichmentTimestamp = enrichmentTimestamp;
        this.processingTimeMs = processingTimeMs;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(final String tradeId) {
        this.tradeId = tradeId;
    }

    public Map<String, Object> getMarketData() {
        return marketData;
    }

    public void setMarketData(final Map<String, Object> marketData) {
        this.marketData = marketData;
    }

    public Map<String, Object> getRiskMetrics() {
        return riskMetrics;
    }

    public void setRiskMetrics(final Map<String, Object> riskMetrics) {
        this.riskMetrics = riskMetrics;
    }

    public Map<String, Object> getPricingData() {
        return pricingData;
    }

    public void setPricingData(final Map<String, Object> pricingData) {
        this.pricingData = pricingData;
    }

    public String getEnrichmentStatus() {
        return enrichmentStatus;
    }

    public void setEnrichmentStatus(final String enrichmentStatus) {
        this.enrichmentStatus = enrichmentStatus;
    }

    public LocalDateTime getEnrichmentTimestamp() {
        return enrichmentTimestamp;
    }

    public void setEnrichmentTimestamp(final LocalDateTime enrichmentTimestamp) {
        this.enrichmentTimestamp = enrichmentTimestamp;
    }

    public Long getProcessingTimeMs() {
        return processingTimeMs;
    }

    public void setProcessingTimeMs(final Long processingTimeMs) {
        this.processingTimeMs = processingTimeMs;
    }
}