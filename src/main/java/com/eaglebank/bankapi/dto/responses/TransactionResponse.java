package com.eaglebank.bankapi.dto.responses;

import java.time.OffsetDateTime;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TransactionResponse {

    @NotNull
    @Pattern(regexp = "^tan-[A-Za-z0-9]+$")
    private String id;

    @NotNull
    private Double amount;

    @NotNull
    private String type;

    private String reference;

    @NotNull
    @Pattern(regexp = "^usr-[A-Za-z0-9]+$")
    private String userId;

    @NotNull
    private OffsetDateTime createdTimestamp;

}

