package com.example.softwareproject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
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


public class fragment_followers extends Fragment {
    LinearLayout LayoutFollowers;//layout for fragment
    String user,curr_user;//string to store user
    UI_Views views = new UI_Views();//ui_view callss to create views
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_followers, container, false);//setting fragmet view from inflator
        Intent intent = getActivity().getIntent();//getting var from previios activity
        user = intent.getStringExtra("username");//getting username
        curr_user = intent.getStringExtra("loggedinuser");//getting main user
        LayoutFollowers = (LinearLayout) v.findViewById(R.id.layoutFollowers);//setting up layout
        processUsers();//function to show followers
        Search_User_class su = new Search_User_class();//class to search for who is user following

        return v;
    }

    public void processUsers(){//fucntion to process the user
        LayoutFollowers.removeAllViews();
        int index = 0;
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("social")//firebase ref to social tabl in database
                .child(user).child("followers");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    int index = 0;//no of folowers
                    for(DataSnapshot data:snapshot.getChildren()){
                        TextView df = views.UserList(getContext(), data.getValue(String.class));
                        df.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(), user_display.class);
                                intent.putExtra("username", data.getValue(String.class));//sending daa to next activity
                                intent.putExtra("loggedinuser",curr_user);//sending daa to next activity
                                getActivity().startActivity(intent);
                                getActivity().finish();//starting new activity
                            }
                        });
                        index++;
                        LayoutFollowers.addView(df);
                        LayoutFollowers.addView(views.Divider(getContext()));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}