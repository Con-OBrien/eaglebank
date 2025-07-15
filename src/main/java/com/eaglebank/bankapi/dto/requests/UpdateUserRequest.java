package com.eaglebank.bankapi.dto.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {

    private String name;

    @Email(message = "Email should be valid")
    private String email;

    private String phoneNumber;

    private String password;

    @Valid
    private AddressRequest address;

}
