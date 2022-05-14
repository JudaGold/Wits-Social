package com.example.softwareproject;

import android.widget.EditText;

public class Field_validations {
    public  boolean passwords_match(String pw1, String pw2, EditText Text_filed){
        if(!pw1.equals(pw2)){
            Text_filed.setError("Passwords do not match");
            return false;
        }
        return true;
    }

    public boolean valid_password(String password,EditText Text_filed){
        if(password.length()<=7){
            Text_filed.setError("Password needs to be of length 8 or more");
            return false;
        }else{
            return true;
        }
    }
    public boolean Valid_number(String num,EditText Text_filed) {
        if (num.length() !=10) {
            Text_filed.setError("invalid phone number");
            return false;
        } else {
            return true;
        }
    }
    public boolean check_email(String email,EditText Text_filed) {
        if (email.contains("@")) {
            return true;
        } else {
            Text_filed.setError("Invalid email address");
            return false;
        }
    }

    public boolean bioValidation(String userBio,EditText Text_filed)
    {
        boolean bioValid = true;
        if (userBio.length() > 50)
        {
            Text_filed.setError("Bio too long (it needs to be less than 50 characters)");
            bioValid = false;
        }
        return bioValid;
    }

    public boolean validateInput(String username, String password,EditText Text_filed1,EditText Text_filed2)
    {
        boolean validInput = true;
        if (password.equals("")){
            validInput= false;
            Text_filed1.setError("Password cannot be empty");
        }

        if (username.equals("")){
            validInput= false;
            Text_filed2.setError("Username cannot be empty");
        }
        return validInput;
    }


}
