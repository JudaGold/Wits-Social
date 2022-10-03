package com.example.softwareproject;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import okhttp3.internal.cache.DiskLruCache;


public class fragment_Favourites extends Fragment {
    String curr_user;
    FirebaseDatabase fb;
    DatabaseReference dr;
    LinearLayout l;
    UI_Views views;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment__favourites, container, false);
        Intent intent = getActivity().getIntent();
        curr_user = intent.getStringExtra("username");
        l = (LinearLayout) v.findViewById(R.id.layout_favourites);
        l.setOrientation(LinearLayout.VERTICAL);

        dr = fb.getInstance().getReference("FavouritePosts").child(curr_user);
        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                        String post_user = dataSnapshot.getKey();
                        long count = dataSnapshot.child("ID").getChildrenCount();
                        for(int i = 1;i<=count;i++){
                            String id = dataSnapshot.child("ID").child(String.valueOf(i)).getValue(String.class);
                            add_post(post_user,id);
                        }

                    }
                }else{
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return v;
    }

    void add_post(String user_post, String post_id){
        DatabaseReference bd = FirebaseDatabase.getInstance().getReference("Posts").child(user_post).child(post_id);
        bd.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String body = snapshot.child("body").getValue(String.class);
                    String image = snapshot.child("post_image_url").getValue(String.class);
                    String time = snapshot.child("time").getValue(String.class);
                    display_post(user_post,body,image,time);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void display_post(String user_post, String body,String image,String time){
        views = new UI_Views();
        LinearLayout lh = views.createPostLayout(getContext());

        l.addView(views.createUsernameTextView(getContext(),user_post));
        lh.addView(views.createTimeTextView(getContext(),time));
        if(!image.equalsIgnoreCase("")){
            lh.addView(views.createImageView(getContext(),getActivity(),image));
        }
        lh.addView(views.createBodyTextView(getContext(),getActivity(),body));
        l.addView(lh);
        l.addView(views.addSpace(getContext()));
    }
}