package com.example.softwareproject;

import static org.junit.jupiter.api.Assertions.*;

import android.widget.EditText;

import org.junit.jupiter.api.Test;

public class Field_Validations_Tests {
    Field_Validations field_validations = new Field_Validations();
    EditText et;

    @Test
    public void checkPasswordsMatch_validMatch() {
        String fake_password1 = "David2001";
        String fake_password2 = "David2001";
        boolean correctMatch = field_validations.passwords_match(fake_password1, fake_password2, et);

        assertTrue(correctMatch);
    }

    @Test
    public void checkPasswordsMatch_invalidMatch() {
        String fake_password1 = "David2001";
        String fake_password2 = "Dave2001";
        boolean correctMatch = field_validations.passwords_match(fake_password1, fake_password2, et);

        assertFalse(correctMatch);
    }

    @Test
    void valid_password() {
    }

    @Test
    void valid_number() {
    }

    @Test
    void check_email() {
    }

    @Test
    void bioValidation() {
    }

    @Test
    void validateInput() {
    }
}