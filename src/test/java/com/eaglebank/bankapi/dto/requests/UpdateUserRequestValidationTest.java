package com.eaglebank.bankapi.dto.requests;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class UpdateUserRequestValidationTest {

    private static Validator validator;

    @BeforeAll
    public static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private AddressRequest validAddress() {
        AddressRequest address = new AddressRequest();
        address.setLine1("221B Baker Street");
        address.setTown("London");
        address.setCounty("Greater London");
        address.setPostcode("NW1 6XE");
        return address;
    }

    private UpdateUserRequest buildValidRequest() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("Jane Doe");
        request.setEmail("jane.doe@example.com");
        request.setPhoneNumber("01234567890");
        request.setAddress(validAddress());
        return request;
    }

    @Test
    public void allFieldsValid_shouldPassValidation() {
        UpdateUserRequest request = buildValidRequest();

        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    public void emptyRequest_shouldPassValidation() {
        UpdateUserRequest request = new UpdateUserRequest();

        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

        // Nothing required for update — so this should still pass
        assertThat(violations).isEmpty();
    }

    @Test
    public void invalidEmail_shouldFailValidation() {
        UpdateUserRequest request = buildValidRequest();
        request.setEmail("not-an-email");

        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    public void addressMissingRequiredField_shouldFailValidation() {
        UpdateUserRequest request = buildValidRequest();
        request.getAddress().setPostcode(null);

        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().contains("address.postcode"));
    }
}
