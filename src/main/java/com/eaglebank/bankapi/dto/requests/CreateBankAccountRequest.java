package com.eaglebank.bankapi.dto.requests;


import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateBankAccountRequest {

    @NotBlank
    private String name;

    @NotNull
    private AccountType accountType;

    public enum AccountType {
        personal
    }
}

