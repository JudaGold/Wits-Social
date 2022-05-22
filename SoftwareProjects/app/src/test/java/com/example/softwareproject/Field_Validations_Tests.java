package com.example.softwareproject;

import static org.junit.jupiter.api.Assertions.*;

import android.widget.EditText;
import android.content.Context;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.junit.jupiter.api.Test;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class Field_Validations_Tests {
    @Mock
    Context mockContext;

    Field_Validations field_validations = new Field_Validations();
    EditText et = new EditText(mockContext);
    EditText et2 = new EditText(mockContext);

    @Test
    public void passwords_match_insertingPasswords_validMatch() {
        String fake_password1 = "David2001";
        String fake_password2 = "David2001";

        boolean valid_match = field_validations.passwords_match(fake_password1, fake_password2, et);

        assertTrue(valid_match);
    }

    @Test
    public void passwords_match_insertingPasswords_invalidMatch() {
        String fake_password1 = "David2001";
        String fake_password2 = "Dave2001";

        boolean invalid_match = field_validations.passwords_match(fake_password1, fake_password2, et);

        assertFalse(invalid_match);
    }

    @Test
    public void valid_password_insertingPassword_validPassword() {
        String fake_password = "David2001";

        boolean valid_password = field_validations.valid_password(fake_password, et);

        assertTrue(valid_password);
    }

    @Test
    public void valid_password_insertingPassword_invalidPassword() {
        String fake_password = "Dav";

        boolean invalid_password = field_validations.valid_password(fake_password, et);

        assertFalse(invalid_password);
    }


    @Test
    void valid_number_insertingNumber_validNumber() {
       String fake_num = "0234561876";

       boolean valid_number = field_validations.Valid_number(fake_num, et);

       assertTrue(valid_number);
    }

    @Test
    void valid_number_insertingNumber_invalidNumber() {
        String fake_num = "023";

        boolean invalid_number = field_validations.Valid_number(fake_num, et);

        assertFalse(invalid_number);
    }

    @Test
    void check_email_insertingEmail_validEmail() {
        String fake_email = "d@g.com";

        boolean valid_email = field_validations.check_email(fake_email, et);

        assertTrue(valid_email);
    }

    @Test
    void check_email_insertingEmail_invalidEmail() {
        String fake_email = "dg.com";

        boolean invalid_email = field_validations.check_email(fake_email, et);

        assertFalse(invalid_email);
    }

    @Test
    void bioValidation_insertingBio_validBio() {
        String fake_bio = "Life is great!";

        boolean valid_bio = field_validations.bioValidation(fake_bio, et);

        assertTrue(valid_bio);
    }

    @Test
    void bioValidation_insertingBio_invalidBio() {
        String fake_bio = "Life is great! I should write a book about myself. I'll call it David " +
                "and Goliath";

        boolean invalid_bio = field_validations.bioValidation(fake_bio, et);

        assertFalse(invalid_bio);
    }

    @Test
    void validateInput_insertingUsernameAndPassword_validInput() {
        String fake_username = "Dave";
        String fake_password = "dave2001";

        boolean valid_input = field_validations.validateInput(fake_username, fake_password, et,
                et2);

        assertTrue(valid_input);
    }

    @Test
    void validateInput_insertingUsernameAndPassword_invalidUsernameInput() {
        String fake_username = "";
        String fake_password = "dave2001";

        boolean invalid_input = field_validations.validateInput(fake_username, fake_password, et,
                et2);

        assertFalse(invalid_input);
    }

    @Test
    void validateInput_insertingUsernameAndPassword_invalidPasswordInput() {
        String fake_username = "Dave";
        String fake_password = "";

        boolean invalid_input = field_validations.validateInput(fake_username, fake_password, et,
                et2);

        assertFalse(invalid_input);
    }
}