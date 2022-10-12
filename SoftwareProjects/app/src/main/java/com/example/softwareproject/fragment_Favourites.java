package com.example.softwareproject;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
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

        dr = fb.getInstance().getReference("FavouritePosts").child(curr_user);//reference to collect all favourite posts
        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                        String post_user = dataSnapshot.getKey();
                        long count = dataSnapshot.child("ID").getChildrenCount();
                        for(int i = 1;i<=count;i++){//checking multiple likes from a single user
                            String id = dataSnapshot.child("ID").child(String.valueOf(i)).getValue(String.class);
                            add_post(post_user,id);//adding a post to the list of liked posts to display
                        }

                    }
                }else{
                    views = new UI_Views();
                    l.addView(views.createErrorTextView(getContext(),"Nothing to show :(  Try liking a post."));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return v;
    }

    void add_post(String user_post, String post_id){//function to add a post to later display
        DatabaseReference bd = FirebaseDatabase.getInstance().getReference("Posts").child(user_post).child(post_id);
        bd.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String body = snapshot.child("body").getValue(String.class);
                    String image = snapshot.child("post_image_url").getValue(String.class);
                    String time = snapshot.child("time").getValue(String.class);
                    display_post(user_post,body,image,time);//calling function to display post
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void display_post(String user_post, String body,String image,String time){
        views = new UI_Views();//initializing UI_views class to create ui views
        LinearLayout lh = views.createPostLayout(getContext());
        LinearLayout hl = views.createHorizontalLayout(getContext());
        hl.setGravity(Gravity.NO_GRAVITY);

       /* l.addView(views.createUsernameTextView(getContext(),user_post));
        lh.addView(views.createTimeTextView(getContext(),time));*/
        TextView user = views.createUsernameTextView(getContext(),user_post);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(720,ViewGroup.LayoutParams.WRAP_CONTENT);
        user.setLayoutParams(params); //change width of username textview to separate the username and time
        hl.addView(user); //adding posts user
        hl.addView(views.createTimeTextView(getContext(),time.substring(0, 10))); //adding post time
        lh.addView(hl);
        if(!image.equalsIgnoreCase("")){//checking if user posted a image
            lh.addView(views.createImageView(getContext(),getActivity(),image));//adding post image
        }
        lh.addView(views.createBodyTextView(getContext(),getActivity(),body));//adding post body
        l.addView(views.addSpace(getContext()));//adding a space to separate posts
        l.addView(lh);//adding post to main layout

    }
}