package com.example.witssocial;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class sign_up extends AppCompatActivity
{
    EditText edtEmail,edtPhoneNo,edtUsername,edtPassword,edtConfirmPassword,edtFirstName,edtLastName;
    Button btnSignUp;
    ContentValues cv;
    TextView tv,pa;

    FirebaseDatabase fb;
    DatabaseReference Udb,Gdb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edtEmail= (EditText) findViewById(R.id.email_address);
        edtPhoneNo = (EditText) findViewById(R.id.Phone_number);
        edtUsername = (EditText) findViewById(R.id.username);
        edtPassword = (EditText) findViewById(R.id.pass1);
        edtConfirmPassword = (EditText) findViewById(R.id.pass2);
        btnSignUp = (Button) findViewById(R.id.sibt);
        edtFirstName=(EditText) findViewById(R.id.first_name);
        edtLastName=(EditText) findViewById(R.id.last_name);
        tv = (TextView) findViewById(R.id.tv);
        pa = (TextView) findViewById(R.id.pad);



        btnSignUp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String email = edtEmail.getText().toString();
                String number  = edtPhoneNo.getText().toString();
                String username = edtUsername.getText().toString();
                String password = edtPassword.getText().toString();
                String ConfirmPassword = edtConfirmPassword.getText().toString();
                String name = edtFirstName.getText().toString()+" "+edtLastName.getText().toString();

                fb= FirebaseDatabase.getInstance();
                Gdb = fb.getReference("Users");

                boolean completed = completed();
                boolean matchingPassword = passwords_match(password, ConfirmPassword);
                boolean validPassword = valid_password(password);

                if (completed && matchingPassword && validPassword)
                {
                    CreateUserClass createUserClass = new CreateUserClass(username,email,number,password,name);
                    Gdb.child(username).setValue(createUserClass);
                }
            }
        });
    }

    public void check_username(String username){}
    public void check_email(String email){}
    public boolean completed(){
        boolean key = true;
        if(TextUtils.isEmpty(edtUsername.getText().toString())){
            edtUsername.setHint("Please enter in a username");
            key =  false;
        }
         if(TextUtils.isEmpty(edtEmail.getText().toString())){
            edtEmail.setHint("Please enter in an email address");
            key =  false;
        }
         if(TextUtils.isEmpty(edtPhoneNo.getText().toString())){
            edtPhoneNo.setHint("Please enter in a phone number");
            key =  false;
        }
         if(TextUtils.isEmpty(edtFirstName.getText().toString())){
            edtFirstName.setHint("Please enter in your first name");
            key =  false;
        }
         if(TextUtils.isEmpty(edtLastName.getText().toString())){
            edtLastName.setHint("Please enter in your surname ");
            key =  false;
        }
         if(TextUtils.isEmpty(edtPassword.getText().toString())){
            edtPassword.setHint("Please enter in a password ");
            key =  false;
        }
         if(TextUtils.isEmpty(edtConfirmPassword.getText().toString())){
            edtConfirmPassword.setHint("Please enter in a password ");
            key =  false;
        }
         if(key == false){
             tv.setText("Please ensure you have\ncompleted all the fields");
         }
      return key;
    }
    public  boolean passwords_match(String pw1,String pw2){
        if(!pw1.equals(pw2)){
            edtConfirmPassword.setText("");
            edtConfirmPassword.setHint("Passwords do not match");
            return false;
        }
        return true;
    }
    public boolean valid_password(String password){
        boolean valid  = true;
        if(password.length()<=7){
            pa.setText("Password needs to be of length 8 or more");
            valid = false;
        }
        return valid;
    }
}
