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
    LinearLayout layoutFollowing;
    String user;
    String curr_user;
    UI_Views views = new UI_Views();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_following, container, false);
        Intent intent = getActivity().getIntent();
        user = intent.getStringExtra("username");
        curr_user = intent.getStringExtra("loggedinuser");
        layoutFollowing = (LinearLayout) v.findViewById(R.id.layoutFollowing);
        processUsers();
        Search_User_class su = new Search_User_class();
        
        return v;
    }

    public void processUsers(){
        layoutFollowing.removeAllViews();
        int index = 0;
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("social")
                .child(user).child("following");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    int index = 0;
                    for(DataSnapshot data:snapshot.getChildren()){
                        TextView df = views.UserList(getContext(), data.getValue(String.class));
                        df.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(), user_display.class);
                                intent.putExtra("username", data.getValue(String.class));
                                intent.putExtra("loggedinuser",curr_user);
                                getActivity().startActivity(intent);
                                getActivity().finish();
                            }
                        });
                        index++;
                        layoutFollowing.addView(df);
                        layoutFollowing.addView(views.Divider(getContext()));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        }
    }