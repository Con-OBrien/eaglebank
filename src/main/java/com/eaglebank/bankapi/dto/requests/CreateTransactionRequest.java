package com.eaglebank.bankapi.dto.requests;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateTransactionRequest {

    @NotNull
    @DecimalMin("0.00")
    @DecimalMax("10000.00")
    private Double amount;

    @NotBlank
    @Pattern(regexp = "GBP")
    private String currency;

    @NotNull
    private TransactionType type;

    private String reference;

    public enum TransactionType {
        deposit,
        withdrawal
    }
}

