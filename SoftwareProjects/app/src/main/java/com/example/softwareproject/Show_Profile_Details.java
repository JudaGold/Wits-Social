package com.example.softwareproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Show_Profile_Details extends AppCompatActivity
{
    TextView UserName,bio,PhoneNumber,EmailAddress;
    String username;

    FirebaseDatabase fb;
    DatabaseReference Gdb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_profile_details);

        Intent intent = getIntent();
        username = intent.getStringExtra("Username");

        DatabaseReference bd = FirebaseDatabase.getInstance().getReference("Users");

        Query getUserInfo = bd.orderByChild("username").equalTo(username);
        getUserInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    UserName = (TextView) findViewById(R.id.UserName_txt);
                    UserName.setText(snapshot.child(username).child("username").getValue(String.class));

                    bio = (TextView) findViewById(R.id.Bio_txt);
                    bio.setText(snapshot.child(username).child("bio").getValue(String.class));

                    PhoneNumber = (TextView) findViewById(R.id.PhoneNumber_txt);
                    PhoneNumber.setText(snapshot.child(username).child("phoneNumber").getValue(String.class));

                    EmailAddress = (TextView) findViewById(R.id.EmailAddress_txt);
                    EmailAddress.setText(snapshot.child(username).child("email").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }
}
