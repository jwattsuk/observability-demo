package com.jwattsuk.trade.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "trade")
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trade_id", unique = true, nullable = false, length = 50)
    private String tradeId;

    @Column(name = "counterparty", nullable = false, length = 100)
    private String counterparty;

    @Column(name = "instrument_type", nullable = false, length = 50)
    private String instrumentType;

    @Column(name = "underlying_asset", nullable = false, length = 100)
    private String underlyingAsset;

    @Column(name = "notional_amount", nullable = false, precision = 20, scale = 2)
    private BigDecimal notionalAmount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Column(name = "strike_price", precision = 20, scale = 4)
    private BigDecimal strikePrice;

    @Column(name = "maturity_date", nullable = false)
    private LocalDate maturityDate;

    @Column(name = "trade_date", nullable = false)
    private LocalDate tradeDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "direction", nullable = false, length = 10)
    private TradeDirection direction;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TradeStatus status;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "enrichment_data", columnDefinition = "jsonb")
    private Map<String, Object> enrichmentData;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Trade() {
        this.tradeDate = LocalDate.now();
        this.status = TradeStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Trade(final String tradeId, final String counterparty, final String instrumentType,
                 final String underlyingAsset, final BigDecimal notionalAmount, final String currency,
                 final BigDecimal strikePrice, final LocalDate maturityDate, final TradeDirection direction) {
        this();
        this.tradeId = tradeId;
        this.counterparty = counterparty;
        this.instrumentType = instrumentType;
        this.underlyingAsset = underlyingAsset;
        this.notionalAmount = notionalAmount;
        this.currency = currency;
        this.strikePrice = strikePrice;
        this.maturityDate = maturityDate;
        this.direction = direction;
    }

    // Getters and setters
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