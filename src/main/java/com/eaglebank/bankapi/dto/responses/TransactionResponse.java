package com.eaglebank.bankapi.dto.responses;

import java.time.OffsetDateTime;
import java.util.UUID;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class TransactionResponse {

    @NotNull
    @Pattern(regexp = "^tan-[A-Za-z0-9]+$")
    private String id;

    @NotNull
    private Double amount;

    @NotNull
    private String currency;  // e.g., "GBP"

    @NotNull
    private String type; // "deposit" or "withdrawal"

    private String reference;

    @NotNull
    @Pattern(regexp = "^usr-[A-Za-z0-9]+$")
    private String userId;

    @NotNull
    private OffsetDateTime createdTimestamp;

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public OffsetDateTime getCreatedTimestamp() { return createdTimestamp; }
    public void setCreatedTimestamp(OffsetDateTime createdTimestamp) { this.createdTimestamp = createdTimestamp; }
}

