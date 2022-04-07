package com.example.witssocial2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LogIn extends AppCompatActivity {
    Button lg;//lg = Log In
    TextView sun;//sun = Sign up now
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in);
        sun = (TextView) findViewById(R.id.SignUpNow);
        sun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LogIn.this,sign_up.class);
                startActivity(intent);
            }
        });
    }
}

