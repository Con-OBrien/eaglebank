package com.eaglebank.bankapi.dto.requests;


import jakarta.validation.constraints.*;

public class CreateBankAccountRequest {

    public enum AccountType {
        PERSONAL;
    }

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Account type is required")
    private AccountType accountType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }
}

