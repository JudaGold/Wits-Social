package com.example.softwareproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class sign_up extends AppCompatActivity {
    EditText st,us,p1,p2,fname,sname;
    Button si;
    ContentValues cv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        st = (EditText) findViewById(R.id.studentnumber);
        us = (EditText) findViewById(R.id.username);
        p1 = (EditText) findViewById(R.id.pass1);
        p2 = (EditText) findViewById(R.id.pass2);
        si = (Button) findViewById(R.id.sibt);
        fname=(EditText) findViewById(R.id.first_name);
        sname=(EditText) findViewById(R.id.last_name);


        si.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(sign_up.this,"success",Toast.LENGTH_LONG).show();
            }
        });


    }


}