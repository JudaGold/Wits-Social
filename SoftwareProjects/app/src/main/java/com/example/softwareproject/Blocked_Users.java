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
    LinearLayout l;
    String username;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //setContentView(R.id.Blocked_Users);
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        username = intent.getStringExtra("Username");

//        l = findViewById(R.id.Blocked_Users);
        l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(1200, 150, 1.5f);
//        l.setLayoutParams(lp);
//        l.setBackgroundColor(Color.BLACK);
        setContentView(l);
        processUsers();


    }

    public void processUsers(){
        l.removeAllViews();
        TextView t = new TextView(this);
        t.setText("Blocked Users");
        t.setTextSize(20);
        t.setPadding(350,15,0,30);
        t.setTextColor(Color.parseColor("yellow"));
        t.setHeight(140);
        t.setBackgroundColor(Color.parseColor("blue"));
        t.setGravity(Gravity.CENTER_VERTICAL);
        l.addView(t);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("social")
                .child(username).child("Blocking");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    int index = 0;
                    for(DataSnapshot data:snapshot.getChildren()){
                        TextView df = new TextView(getApplicationContext());
                        df.setText(data.getValue(String.class));
                        df.setTextSize(20);
                        df.setPadding(30,15,0,30);
                        df.setTextColor(Color.parseColor("white"));
                        df.setHeight(140);
                        df.setBackgroundColor(Color.parseColor("#F51E1B1B"));
                        df.setGravity(Gravity.CENTER_VERTICAL);
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
                        l.addView(Divider());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public View Divider(){
        View viewDivider = new View(getContext());
        int dividerHeight = 4;
        viewDivider.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dividerHeight));
        viewDivider.setBackgroundColor(Color.parseColor("#A417AFEA"));
        return  viewDivider;
    }
}
