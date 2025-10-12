package com.jwattsuk.trade.dto;

import com.jwattsuk.trade.entity.TradeDirection;
import com.jwattsuk.trade.entity.TradeStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

public class TradeResponse {

    private Long id;
    private String tradeId;
    private String counterparty;
    private String instrumentType;
    private String underlyingAsset;
    private BigDecimal notionalAmount;
    private String currency;
    private BigDecimal strikePrice;
    private LocalDate maturityDate;
    private LocalDate tradeDate;
    private TradeDirection direction;
    private TradeStatus status;
    private Map<String, Object> enrichmentData;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TradeResponse() {
    }

    public TradeResponse(final Long id, final String tradeId, final String counterparty, final String instrumentType,
                        final String underlyingAsset, final BigDecimal notionalAmount, final String currency,
                        final BigDecimal strikePrice, final LocalDate maturityDate, final LocalDate tradeDate,
                        final TradeDirection direction, final TradeStatus status, final Map<String, Object> enrichmentData,
                        final LocalDateTime createdAt, final LocalDateTime updatedAt) {
        this.id = id;
        this.tradeId = tradeId;
        this.counterparty = counterparty;
        this.instrumentType = instrumentType;
        this.underlyingAsset = underlyingAsset;
        this.notionalAmount = notionalAmount;
        this.currency = currency;
        this.strikePrice = strikePrice;
        this.maturityDate = maturityDate;
        this.tradeDate = tradeDate;
        this.direction = direction;
        this.status = status;
        this.enrichmentData = enrichmentData;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(final String tradeId) {
        this.tradeId = tradeId;
    }

    public String getCounterparty() {
        return counterparty;
    }

    public void setCounterparty(final String counterparty) {
        this.counterparty = counterparty;
    }

    public String getInstrumentType() {
        return instrumentType;
    }

    public void setInstrumentType(final String instrumentType) {
        this.instrumentType = instrumentType;
    }

    public String getUnderlyingAsset() {
        return underlyingAsset;
    }

    public void setUnderlyingAsset(final String underlyingAsset) {
        this.underlyingAsset = underlyingAsset;
    }

    public BigDecimal getNotionalAmount() {
        return notionalAmount;
    }

    public void setNotionalAmount(final BigDecimal notionalAmount) {
        this.notionalAmount = notionalAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(final String currency) {
        this.currency = currency;
    }

    public BigDecimal getStrikePrice() {
        return strikePrice;
    }

    public void setStrikePrice(final BigDecimal strikePrice) {
        this.strikePrice = strikePrice;
    }

    public LocalDate getMaturityDate() {
        return maturityDate;
    }

    public void setMaturityDate(final LocalDate maturityDate) {
        this.maturityDate = maturityDate;
    }

    public LocalDate getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(final LocalDate tradeDate) {
        this.tradeDate = tradeDate;
    }

    public TradeDirection getDirection() {
        return direction;
    }

    public void setDirection(final TradeDirection direction) {
        this.direction = direction;
    }

    public TradeStatus getStatus() {
        return status;
    }

    public void setStatus(final TradeStatus status) {
        this.status = status;
    }

    public Map<String, Object> getEnrichmentData() {
        return enrichmentData;
    }

    public void setEnrichmentData(final Map<String, Object> enrichmentData) {
        this.enrichmentData = enrichmentData;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(final LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}