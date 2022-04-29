package com.example.softwareproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity
{

    /*Declaration of variables- to store form information*/
    EditText textInputEditTextUsername, textInputEditTextPassword;
    Button buttonLogIn;
    TextView signup,forgotpassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Save  form info into the variables declared on line 16-17*/
        textInputEditTextUsername = (EditText) findViewById(R.id.UserNameToLogIn);
        textInputEditTextPassword = (EditText) findViewById(R.id.password);
        buttonLogIn = (Button) findViewById(R.id.btnLogin);
        signup = (TextView) findViewById(R.id.TSI);
        forgotpassword = (TextView) findViewById(R.id.fp);


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,sign_up.class);
                startActivity(intent);

            }
        });

        buttonLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Convert the values to strings*/
                String username, password;
                username = textInputEditTextUsername.getText().toString();
                password = textInputEditTextPassword.getText().toString();

                boolean validInput = validateInput(username,password);
                if (validInput){
                    isUser(username,password);

                }

            }
        });
        try{
            Intent intent = getIntent();
            String u = intent.getStringExtra("Username");
            textInputEditTextUsername.setText(u);

        }catch(Exception e){}

        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Forgot_password.class);
                startActivity(intent);
            }
        });
    }


    /*Checks if the user info is in the database*/
    public void isUser(String username, String UserPassword)
    {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        Query checkUser = reference.orderByChild("username").equalTo(username);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    // textInputEditTextUsername.setError(null);

                    String DBPassword = snapshot.child(username).child("password").getValue(String.class); //extracts password from database
                    if (DBPassword.equals(UserPassword))
                    {
                        Intent intent = new Intent(MainActivity.this,main_profile.class);
                        textInputEditTextPassword.setText("");
                        intent.putExtra("username",username);
                        startActivity(intent);
                    }
                    else
                    {
                        textInputEditTextPassword.setError("The password is incorrect");
                    }
                }
                else
                {
                    textInputEditTextUsername.setError("The user does not exists");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    public boolean validateInput(String password, String username)
    {
        boolean validInput = true;
        if (password.equals("")){
            validInput= false;
            textInputEditTextPassword.setError("Password cannot be empty");
        }

        if (username.equals("")){
            validInput= false;
            textInputEditTextUsername.setError("Username cannot be empty");
        }
        return validInput;
    }





}
