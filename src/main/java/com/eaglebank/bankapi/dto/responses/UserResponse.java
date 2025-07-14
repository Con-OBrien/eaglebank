package com.eaglebank.bankapi.dto.responses;

import java.time.OffsetDateTime;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class UserResponse {

    @NotNull
    @Pattern(regexp = "^usr-[A-Za-z0-9]+$")
    private String id;

    @NotNull
    private String name;

    @NotNull
    private AddressResponse address;

    @NotNull
    private String phoneNumber;  // Assuming stored as string with format validation

    @NotNull
    @Email
    private String email;

    @NotNull
    private OffsetDateTime createdTimestamp;

    @NotNull
    private OffsetDateTime updatedTimestamp;

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public AddressResponse getAddress() { return address; }
    public void setAddress(AddressResponse address) { this.address = address; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public OffsetDateTime getCreatedTimestamp() { return createdTimestamp; }
    public void setCreatedTimestamp(OffsetDateTime createdTimestamp) { this.createdTimestamp = createdTimestamp; }

    public OffsetDateTime getUpdatedTimestamp() { return updatedTimestamp; }
    public void setUpdatedTimestamp(OffsetDateTime updatedTimestamp) { this.updatedTimestamp = updatedTimestamp; }
}
