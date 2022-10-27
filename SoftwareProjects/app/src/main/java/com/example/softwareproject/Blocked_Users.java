package com.example.softwareproject;

import static androidx.test.InstrumentationRegistry.getContext;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Blocked_Users extends AppCompatActivity {
    LinearLayout l;//layout of fragment
    String username;//string to store username of the main user running the app
    UI_Views views = new UI_Views();//instantiating a ui_views class to sut up various view for fragment

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //setContentView(R.id.Blocked_Users);
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        username = intent.getStringExtra("Username");//getting the user who is running the app

        //l = (LinearLayout) findViewById(R.id.Blocked_Users);
        l = new LinearLayout(this);//layout for this activity
        l.setOrientation(LinearLayout.VERTICAL);//setting orientation to vertical for the linear layout
        setContentView(l);//setting up l as main layout for fragment
        l.setBackgroundResource(R.drawable.blue_and_grey_background);//setting background color
        processUsers();//function to
    }

    public void processUsers(){
        l.removeAllViews();
        TextView t = new TextView(this);//creating anew text view
        t.setText("Blocked Users");//setting text
        t.setTextSize(20);//text size
        t.setPadding(350,15,0,30);//padding for text views
        t.setTextColor(Color.parseColor("white"));//color of text insode the text view
        t.setHeight(140);//hight of text view
        t.setBackgroundColor(Color.parseColor("#CB17AFEA"));
        t.setGravity(Gravity.CENTER_VERTICAL);
        l.addView(t);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("social")//firebase re fto get all blacked users
                .child(username).child("Blocking");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    int index = 0;
                    for(DataSnapshot data:snapshot.getChildren()){
                        TextView df = views.UserList(getApplicationContext(), data.getValue(String.class));
//                        df.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Intent intent = new Intent(Blocked_Users.this, user_display.class);
//                                intent.putExtra("username", data.getValue(String.class));
//                                intent.putExtra("loggedinuser",username);
//                                startActivity(intent);
//                                finish();
//                            }
//                        });
                        index++;
                        l.addView(df);
                        l.addView(views.Divider(getApplicationContext()));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
