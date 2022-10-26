package com.example.softwareproject;

import static android.app.PendingIntent.getActivity;
import static androidx.test.InstrumentationRegistry.getContext;

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
    DatabaseReference bd = FirebaseDatabase.getInstance().getReference("Users");
    LinearLayout l;
    String username;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.blocked_users);

        Intent intent = getIntent();
        username = intent.getStringExtra("Username");

        l = findViewById(R.id.Blocked_Users);
        l.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(1000,150,1.5f);
        l.setLayoutParams(lp);

        DatabaseReference bdBlock = FirebaseDatabase.getInstance()
                .getReference("social").child(username).child("Blocking");
        bdBlock.addListenerForSingleValueEvent(new ValueEventListener() {//setting a new listener to access the database
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int index = 0;
                for(DataSnapshot data: snapshot.getChildren()) {
                    TextView df = new TextView(getContext());
                    df.setText(data.getValue(String.class));
                    df.setTextSize(20);
                    df.setPadding(30,15,0,30);
                    df.setTextColor(Color.parseColor("white"));
                    df.setHeight(140);
                    df.setBackgroundColor(Color.parseColor("#F51E1B1B"));
                    df.setGravity(Gravity.CENTER_VERTICAL);
                    df.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Blocked_Users.this, user_display.class);
                            intent.putExtra("username", data.getValue(String.class));
                            intent.putExtra("loggedinuser",username);
                            startActivity(intent);
                        }
                    });
                    index++;
                    l.addView(df);
                    l.addView(Divider());
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
