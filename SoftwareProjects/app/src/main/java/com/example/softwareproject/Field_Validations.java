package com.example.softwareproject;

import android.widget.EditText;

public class Field_Validations {
    //Check if the two password fields match
    public  boolean passwords_match(String pw1, String pw2, EditText Text_filed){
        if(!pw1.equals(pw2)){
            Text_filed.setError("Passwords do not match");
            return false;
        }
        return true;
    }

    //Validate Password
    public boolean valid_password(String password,EditText Text_filed){
        if(password.length()<=7){ // Password constraint
            Text_filed.setError("Password needs to be of length 8 or more");
            return false;
        }else{
            return true;
        }
    }

    //Validate Phone number
    public boolean Valid_number(String num,EditText Text_filed) {
        if (num.length() !=10) { // Phone number constraint
            Text_filed.setError("invalid phone number");
            return false;
        } else {
            return true;
        }
    }

    //Validate email Address
    public boolean check_email(String email,EditText Text_filed) {
        if (email.contains("@")) { // Email address constraint
            return true;
        } else {
            Text_filed.setError("Invalid email address");
            return false;
        }
    }

    //Validate bio
    public boolean bioValidation(String userBio,EditText Text_filed)
    {
        boolean bioValid = true;
        if (userBio.length() > 50) //Bio constraint
        {
            Text_filed.setError("Bio too long (it needs to be less than 50 characters)");
            bioValid = false;
        }
        return bioValid;
    }

    //Validate profile input (e.g. username)
    public boolean validateInput(String username, String password,EditText Text_filed1,EditText Text_filed2)
    {
        boolean validInput = true;
        if (password.equals("")){ //Password constraint
            validInput= false;
            Text_filed1.setError("Password cannot be empty");
        }

        if (username.equals("")){ //Username constraint
            validInput= false;
            Text_filed2.setError("Username cannot be empty");
        }
        return validInput;
    }


}
