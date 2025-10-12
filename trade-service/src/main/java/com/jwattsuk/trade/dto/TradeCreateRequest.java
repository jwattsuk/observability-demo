package com.jwattsuk.trade.dto;

import com.jwattsuk.trade.entity.TradeDirection;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TradeCreateRequest {

    @NotBlank(message = "Trade ID is required")
    private String tradeId;

    @NotBlank(message = "Counterparty is required")
    private String counterparty;

    @NotBlank(message = "Instrument type is required")
    private String instrumentType;

    @NotBlank(message = "Underlying asset is required")
    private String underlyingAsset;

    @NotNull(message = "Notional amount is required")
    @Positive(message = "Notional amount must be positive")
    private BigDecimal notionalAmount;

    @NotBlank(message = "Currency is required")
    private String currency;

    private BigDecimal strikePrice;

    @NotNull(message = "Maturity date is required")
    private LocalDate maturityDate;

    @NotNull(message = "Direction is required")
    private TradeDirection direction;

    public TradeCreateRequest() {
    }

    public TradeCreateRequest(final String tradeId, final String counterparty, final String instrumentType,
                             final String underlyingAsset, final BigDecimal notionalAmount, final String currency,
                             final BigDecimal strikePrice, final LocalDate maturityDate, final TradeDirection direction) {
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

    public TradeDirection getDirection() {
        return direction;
    }

    public void setDirection(final TradeDirection direction) {
        this.direction = direction;
    }
}