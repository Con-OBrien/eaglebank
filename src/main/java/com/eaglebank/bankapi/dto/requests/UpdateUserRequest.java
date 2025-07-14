package com.eaglebank.bankapi.dto.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {

    private String name;

    @Valid
    private AddressRequest address;

    private String phoneNumber;

    @Email
    private String email;
}
