package com.eaglebank.bankapi.dto.responses;

import java.util.List;
import jakarta.validation.constraints.NotNull;

public class ListBankAccountsResponse {

    @NotNull
    private List<BankAccountResponse> accounts;

    public List<BankAccountResponse> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<BankAccountResponse> accounts) {
        this.accounts = accounts;
    }
}
