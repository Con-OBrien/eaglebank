package com.eaglebank.bankapi.dto.requests;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateUserRequestValidationTest {

    private static Validator validator;

    @BeforeAll
    public static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private CreateUserRequest buildValidRequest() {
        AddressRequest address = new AddressRequest();
        address.setLine1("123 Main Street");
        address.setTown("Springfield");
        address.setCounty("Anyshire");
        address.setPostcode("AB12 3CD");

        CreateUserRequest request = new CreateUserRequest();
        request.setName("John Doe");
        request.setEmail("john.doe@example.com");
        request.setPhoneNumber("01234567890");
        request.setAddress(address);

        return request;
    }

    @Test
    public void validRequest_shouldPassValidation() {
        CreateUserRequest request = buildValidRequest();

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    public void missingName_shouldFailValidation() {
        CreateUserRequest request = buildValidRequest();
        request.setName(null);

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name"));
    }

    @Test
    public void invalidEmail_shouldFailValidation() {
        CreateUserRequest request = buildValidRequest();
        request.setEmail("invalid-email");

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    public void missingAddress_shouldFailValidation() {
        CreateUserRequest request = buildValidRequest();
        request.setAddress(null);

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("address"));
    }

    @Test
    public void missingAddressLine1_shouldFailValidation() {
        CreateUserRequest request = buildValidRequest();
        request.getAddress().setLine1(null);

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().contains("address.line1"));
    }

    @Test
    public void missingPostcode_shouldFailValidation() {
        CreateUserRequest request = buildValidRequest();
        request.getAddress().setPostcode(null);

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().contains("address.postcode"));
    }
}
