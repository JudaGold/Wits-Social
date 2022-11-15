
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
    Context mockContext; // Creating a fake context

    Field_Validations field_validations = new Field_Validations(); /* Initialising an object for the
                                                                      to Field_Validations class to
    // Creating fake edittexts                                                                  use its methods*/
    EditText et = new EditText(mockContext);
    EditText et2 = new EditText(mockContext);

    // Testing if passwords_match returns true for matching passwords
    @Test
    public void passwords_match_insertingPasswords_validMatch() {
        // Initialising fake passwords
        String fake_password1 = "David2001";
        String fake_password2 = "David2001";

        // Calling the passwords_match method
        boolean valid_match = field_validations.passwords_match(fake_password1, fake_password2, et);

        // Verifying the return value
        assertTrue(valid_match);
    }

    // Testing if passwords_match returns false for passwords that do not match
    @Test
    public void passwords_match_insertingPasswords_invalidMatch() {
        // Initialising fake passwords
        String fake_password1 = "David2001";
        String fake_password2 = "Dave2001";

        // Calling the passwords_match method
        boolean invalid_match = field_validations.passwords_match(fake_password1, fake_password2, et);

        // Verifying the return value
        assertFalse(invalid_match);
    }

    // Testing if valid_password returns true for a valid password
    // Valid password: The password needs have more than 7 characters
    @Test
    public void valid_password_insertingPassword_validPassword() {
        // Initialising a fake valid password
        String fake_password = "David2001";

        // Calling the valid_password method
        boolean valid_password = field_validations.valid_password(fake_password, et);

        // Verifying the return value
        assertTrue(valid_password);
    }

    // Testing if valid_password returns false for a invalid password
    @Test
    public void valid_password_insertingPassword_invalidPassword() {
        // Initialising a fake invalid password
        String fake_password = "Dav";

        // Calling the valid_password method
        boolean invalid_password = field_validations.valid_password(fake_password, et);

        // Verifying the return value
        assertFalse(invalid_password);
    }

    // Testing if Valid_number returns true for a valid number
    // Valid number: The length of the number needs to be 10
    @Test
    void valid_number_insertingNumber_validNumber() {
        // Initialising a fake valid number
        String fake_num = "0234561876";

        // Calling the Valid_number method
        boolean valid_number = field_validations.Valid_number(fake_num, et);

        // Verifying the return value
        assertTrue(valid_number);
    }

    // Testing if Valid_number returns false for a invalid number
    @Test
    void valid_number_insertingNumber_invalidNumber() {
        // Initialising a fake invalid number
        String fake_num = "023";

        // Calling the Valid_number method
        boolean invalid_number = field_validations.Valid_number(fake_num, et);

        // Verifying the return value
        assertFalse(invalid_number);
    }

    // Testing if check_email returns true for a valid email address
    // Valid email: The email address must contain the character '@'
    @Test
    void check_email_insertingEmail_validEmail() {
        // Initialising a fake valid email address
        String fake_email = "d@gmail.com";

        // Calling the check_email method
        boolean valid_email = field_validations.check_email(fake_email, et);

        // Verifying the return value
        assertTrue(valid_email);
    }

    // Testing if check_email returns false for a invalid email address
    @Test
    void check_email_insertingEmail_invalidEmail() {
        // Initialising a fake invalid email address
        String fake_email = "dgmail.com";

        // Calling the check_email method
        boolean invalid_email = field_validations.check_email(fake_email, et);

        // Verifying the return value
        assertFalse(invalid_email);
    }

    // Testing if bioValidation returns true for a valid bio
    // Valid bio: The length of the bio needs be less than 50
    @Test
    void bioValidation_insertingBio_validBio() {
        // Initialising a fake valid bio
        String fake_bio = "Life is great!";

        // Calling the bioValidation method
        boolean valid_bio = field_validations.bioValidation(fake_bio, et);

        // Verifying the return value
        assertTrue(valid_bio);
    }

    // Testing if bioValidation returns false for a invalid bio
    @Test
    void bioValidation_insertingBio_invalidBio() {
        // Initialising a fake invalid bio
        String fake_bio = "Life is great! I should write a book about myself. I'll call it David " +
                "and Goliath";

        // Calling the bioValidation method
        boolean invalid_bio = field_validations.bioValidation(fake_bio, et);

        // Verifying the return value
        assertFalse(invalid_bio);
    }

    // Testing if validateInput returns true for a valid username and a valid password
    // Valid username and password: Both username and password can not be empty
    @Test
    void validateInput_insertingUsernameAndPassword_validInput() {
        // Initialising a fake valid username
        String fake_username = "Dave";
        // Initialising a fake valid password
        String fake_password = "dave2001";

        // Calling the validateInput method
        boolean valid_input = field_validations.validateInput(fake_username, fake_password, et,
                et2);

        // Verifying the return value
        assertTrue(valid_input);
    }

    // Testing if validateInput returns false for an invalid username and a valid password
    @Test
    void validateInput_insertingUsernameAndPassword_invalidUsernameInput() {
        // Initialising a fake invalid username
        String fake_username = "";
        // Initialising a fake valid password
        String fake_password = "dave2001";

        // Calling the validateInput method
        boolean invalid_input = field_validations.validateInput(fake_username, fake_password, et,
                et2);

        // Verifying the return value
        assertFalse(invalid_input);
    }

    // Testing if validateInput returns false for a valid username and an invalid password
    @Test
    void validateInput_insertingUsernameAndPassword_invalidPasswordInput() {
        // Initialising a fake valid username
        String fake_username = "Dave";
        // Initialising a fake invalid password
        String fake_password = "";

        // Calling the validateInput method
        boolean invalid_input = field_validations.validateInput(fake_username, fake_password, et,
                et2);

        // Verifying the return value
        assertFalse(invalid_input);
    }

    // Testing if validateInput returns false for an invalid username and an invalid password
    @Test
    void validateInput_insertingUsernameAndPassword_invalidUsernamePasswordInput() {
        // Initialising a fake invalid username
        String fake_username = "";
        // Initialising a fake invalid password
        String fake_password = "";

        // Calling the validateInput method
        boolean invalid_input = field_validations.validateInput(fake_username, fake_password, et,
                et2);

        // Verifying the return value
        assertFalse(invalid_input);
    }
}