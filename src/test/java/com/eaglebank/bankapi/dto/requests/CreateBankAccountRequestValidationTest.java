package com.eaglebank.bankapi.dto.requests;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CreateBankAccountRequestValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenAccountTypeIsNull_thenValidationFails() {
        CreateBankAccountRequest request = new CreateBankAccountRequest();
        request.setName("My Account");

        Set<ConstraintViolation<CreateBankAccountRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenAllFieldsAreValid_thenValidationPasses() {
        CreateBankAccountRequest request = new CreateBankAccountRequest();
        request.setName("Day to Day");
        request.setAccountType(CreateBankAccountRequest.AccountType.PERSONAL);

        Set<ConstraintViolation<CreateBankAccountRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }
}
