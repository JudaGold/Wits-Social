package com.example.softwareproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class sign_up extends AppCompatActivity {
    EditText em,pn,us,p1,p2,fn,sn;
    Button si;
    ContentValues cv;
    TextView tv,pa;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        em= (EditText) findViewById(R.id.email_address);
        pn = (EditText) findViewById(R.id.Phone_number);
        us = (EditText) findViewById(R.id.username);
        p1 = (EditText) findViewById(R.id.pass1);
        p2 = (EditText) findViewById(R.id.pass2);
        si = (Button) findViewById(R.id.sibt);
        fn=(EditText) findViewById(R.id.first_name);
        sn=(EditText) findViewById(R.id.last_name);
        tv = (TextView) findViewById(R.id.tv);
        pa = (TextView) findViewById(R.id.pad);

        String email = " "+em;
        String pnumber  = ""+pn;
        String username = " "+us;
        String pass1 = " " +p1;
        String pass2 = " "+p2;
        String fname =""+fn;
        String sname =""+sn;


        si.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean valid = false;


                valid = completed();
                valid = valid_password(pass1);
                valid = passwords_match(pass1,pass2);


                 if(valid){
                    Toast.makeText(sign_up.this, "success", Toast.LENGTH_LONG).show();
                }
                 else{
                     Toast.makeText(sign_up.this, "Please fill in all the fields", Toast.LENGTH_LONG).show();
                 }


            }
        });
    }
    public void check_username(String username){}
    public void check_email(String email){}
    public boolean completed(){
        boolean key = true;
        if(TextUtils.isEmpty(us.getText().toString())){
            us.setHint("Please enter in a username");
            key =  false;
        }
         if(TextUtils.isEmpty(em.getText().toString())){
            em.setHint("Please enter in an email address");
            key =  false;
        }
         if(TextUtils.isEmpty(pn.getText().toString())){
            pn.setHint("Please enter in a phone number");
            key =  false;
        }
         if(TextUtils.isEmpty(fn.getText().toString())){
            fn.setHint("Please enter in your first name");
            key =  false;
        }
         if(TextUtils.isEmpty(sn.getText().toString())){
            sn.setHint("Please enter in your surname ");
            key =  false;
        }
         if(TextUtils.isEmpty(p1.getText().toString())){
            p1.setHint("Please enter in a password ");
            key =  false;
        }
         if(TextUtils.isEmpty(p2.getText().toString())){
            p2.setHint("Please enter in a password ");
            key =  false;
        }
         if(key == false){
             tv.setText("Please ensure you have\ncompleted all the fields");
         }
      return key;
    }
    public  boolean passwords_match(String pw1,String pw2){
        if(pw1 != pw2){
            p2.setHint("Passwords do not match");
            return false;
        }
        return true;
    }
    public boolean valid_password(String a){
        boolean valid  = true;
        if(a.length()<=7){
            pa.setText("Password needs to be of length 8 or more");
            valid = false;
        }
        return valid;
    }
}
