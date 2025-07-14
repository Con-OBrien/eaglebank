package com.eaglebank.bankapi.dto.requests;


import lombok.Data;

@Data
public class UpdateBankAccountRequest {

    private String name;

    private CreateBankAccountRequest.AccountType accountType;
}

