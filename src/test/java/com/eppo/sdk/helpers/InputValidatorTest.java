package com.eppo.sdk.helpers;

import com.eppo.sdk.exception.InvalidInputException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InputValidatorTest {

    @DisplayName("Test InputValidator.validateNotBlank() with correct Input")
    @Test
    void testValidateNotBlankWithCorrectInput() {
        Assertions.assertTrue( InputValidator.validateNotBlank("testing", "Testing"));
    }

    @DisplayName("Test InputValidator.validateNotBlank() with incorrect Input")
    @Test
    void testValidateNotBlankWithIncorrectInput() {
        Assertions.assertThrows(
                InvalidInputException.class,
                () -> InputValidator.validateNotBlank("", "Testing data is invalid"),
                "Testing data is invalid"
        );
    }

}