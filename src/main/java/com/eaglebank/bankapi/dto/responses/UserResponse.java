package com.eaglebank.bankapi.dto.responses;

import java.time.OffsetDateTime;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserResponse {

    @NotNull
    @Pattern(regexp = "^usr-[A-Za-z0-9]+$")
    private String id;

    @NotNull
    private String name;

    @NotNull
    private AddressResponse address;

    @NotNull
    private String phoneNumber;

    @NotNull
    @Email
    private String email;

    @NotNull
    private OffsetDateTime createdTimestamp;

    @NotNull
    private OffsetDateTime updatedTimestamp;

}
