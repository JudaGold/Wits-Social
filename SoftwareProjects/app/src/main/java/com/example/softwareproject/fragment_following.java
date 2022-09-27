package com.example.softwareproject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Barrier;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class fragment_following extends Fragment {
    LinearLayout l;
    String user;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_following, container, false);
        Intent intent = getActivity().getIntent();
        user = intent.getStringExtra("username");
        l = (LinearLayout) v.findViewById(R.id.LP_following);
        l.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(1000,150,1.5f);
        l.setLayoutParams(lp);

        processUsers();
        Search_User_class su = new Search_User_class();



    return v;
    }
    public void processUsers(){
        l.removeAllViews();
        int index = 0;
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("social")
                .child(user).child("following");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    int index = 0;
                    for(DataSnapshot data:snapshot.getChildren()){
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
                                Intent intent = new Intent(getActivity(), user_display.class);
                                intent.putExtra("username", data.getValue(String.class));
                                intent.putExtra("loggedinuser",user);
                                getActivity().startActivity(intent);
                                getActivity().finish();
                            }
                        });
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
            int dividerHeight = 5;
            viewDivider.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dividerHeight));
            viewDivider.setBackgroundColor(Color.parseColor("#A417AFEA"));
            return  viewDivider;
        }

    }