package com.example.softwareproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

@Generated
public class Main_Activity extends AppCompatActivity
{

    /*Declaration of variables- to store form information*/
    EditText textInputEditTextUsername, textInputEditTextPassword;
    Button buttonLogIn;
    TextView signup,forgotpassword;
    Field_Validations fv;
    User user;
    CheckBox rememberMe;

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
        fv = new Field_Validations();
        user = new User();
        rememberMe = findViewById(R.id.rememberMe);

        SharedPreferences preferences =  getSharedPreferences("checkBox", MODE_PRIVATE);
        String checkbox = preferences.getString("remember", "");
        if(checkbox.equals("true")){
            String username = preferences.getString("username", "");
            String password = preferences.getString("password", "");
            isUser(username,password);
        }else if(checkbox.equals("false")){
            Toast.makeText(this,"Please eneter your details",Toast.LENGTH_SHORT).show();
        }

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main_Activity.this, Sign_Up.class);
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

                boolean validInput = fv.validateInput(username,password,textInputEditTextPassword,textInputEditTextUsername);
                if (validInput){
                    isUser(username,password);
                }

            }
        });

        rememberMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()){
                    String username;
                    String password;
                    username = textInputEditTextUsername.getText().toString();
                    password = textInputEditTextPassword.getText().toString();

                    boolean validInput = fv.validateInput(username,password,textInputEditTextPassword,textInputEditTextUsername);
                    if (validInput){
                        SharedPreferences preferences = getSharedPreferences("checkBox", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("remember","true");
                        editor.putString("username",username);
                        editor.putString("password",password);
                        editor.apply();
                    }
                }else if(!compoundButton.isChecked()){
                    SharedPreferences preferences = getSharedPreferences("checkBox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("remember","false");
                    editor.apply();
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
                Intent intent = new Intent(Main_Activity.this, Forgot_Password.class);
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
                        Intent intent = new Intent(Main_Activity.this, Main_Profile.class);
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
}
