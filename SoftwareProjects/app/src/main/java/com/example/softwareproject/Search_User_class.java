package com.example.softwareproject;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Search_User_class {
    private static ArrayList<String> users_hashtags;
    boolean following = false;
    Intent intent;

    public void search(String loggedin_user, String My_username, AutoCompleteTextView ACT, ImageButton btn, Activity activity){
        getUsersHashtags(My_username, loggedin_user);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, users_hashtags);
        ACT.setThreshold(1);
        ACT.setAdapter(adapter);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = ACT.getText().toString();

                if (text.charAt(0) == '#')
                {
                    if (search_user_hashtags(text)) {
                        text = text.substring(1);
                        intent = new Intent(activity, Display_Hashtag_Posts.class);
                        intent.putExtra("username", My_username);
                        intent.putExtra("loggedinuser", loggedin_user);
                        intent.putExtra("hashtag", text);
                        activity.startActivity(intent);
                        activity.finish();
                    } else {
                        ACT.setText("");
                        ACT.setHint("no " + text + " found");
                    }
                }
                else {
                    if (search_user_hashtags(text)) {
                        intent = new Intent(activity, user_display.class);
                        intent.putExtra("username", text);
                        intent.putExtra("loggedinuser", loggedin_user);
                        activity.startActivity(intent);
                        activity.finish();
                    } else {
                        ACT.setText("");
                        ACT.setHint("no user " + text + " found");
                    }
                }

            }
        });
    }

    private void getUsersHashtags(String My_username, String loggedin_user) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        Query userlist = reference.orderByChild("username");
        users_hashtags = new ArrayList<>();
        userlist.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    String username = data.child("username").getValue(String.class);
                    try{
                        if (!username.equalsIgnoreCase(My_username)) {
                            if (!username.equalsIgnoreCase(loggedin_user)) {
                                users_hashtags.add(username);
                            }
                        }
                    }
                    catch(Exception c){}

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Hashtags");
        reference2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    String hashtag = data.getKey();
                    try{
                        users_hashtags.add("#" + hashtag);
                    }
                    catch(Exception c){}

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private boolean search_user_hashtags(String user) {
        for (String item : users_hashtags) {
            if (user.equalsIgnoreCase(item)) {
                return true;
            }
        }
        return false;
    }

    public void follow(String main,String user, String fcm_token){
        follow_user(main,user);
        setFollowing(main,user, fcm_token);

    }
    private void follow_user(String user,String searched_user){
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("social").child(user).child("following");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long maxId = snapshot.getChildrenCount()+1;
                if(snapshot.child(searched_user).exists()){
                    ref.child(String.valueOf(maxId)).setValue(searched_user);
                }
                else{
                    ref.child(String.valueOf(maxId)).setValue(searched_user);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setFollowing(String main_user,String user, String fcm_token){
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("social").child(user).child("followers");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long maxId = snapshot.getChildrenCount()+1;
                if(snapshot.child(main_user).exists()){

                }
                else{
                    ref.child(String.valueOf(maxId)).setValue(main_user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference ref2 = FirebaseDatabase.getInstance()
                .getReference("Notifications").child(user);
        ref2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long maxId = snapshot.getChildrenCount()+1;
                if(snapshot.child(fcm_token).exists()){

                }
                else{
                    ref2.child(String.valueOf(maxId)).setValue(fcm_token);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void unfollow(String main,String user){
        set_unfollow(main,user);
        set_unfollower(main,user);
    }

    private void set_unfollow(String main,String user){
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("social").child(main).child("following");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
              for(DataSnapshot data: snapshot.getChildren()){
                  if(data.getValue(String.class).equalsIgnoreCase(user)){
                      data.getRef().removeValue();
                      break;

                  }
              }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void set_unfollower(String main,String user){
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("social").child(user).child("followers");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data: snapshot.getChildren()){
                    if(data.getValue(String.class).equalsIgnoreCase(main)){
                        data.getRef().removeValue();
                        break;

                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void block_user(String curr_user, String block_user, Button btn){//function to block a user
        DatabaseReference bdBlock = FirebaseDatabase.getInstance()
                .getReference("social").child(curr_user).child("Blocking");
        String buttonText = btn.getText().toString();
        if(buttonText == "Blocked"){
            bdBlock.addListenerForSingleValueEvent(new ValueEventListener() {//setting a new listener to access the database
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot data: snapshot.getChildren()) {
                        if (data.getValue(String.class).equalsIgnoreCase(block_user)) {
                            data.getRef().removeValue();
                            break;

                        }
                    }
                    btn.setText("Block");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else {
            bdBlock.addListenerForSingleValueEvent(new ValueEventListener() {//setting a new listener to access the database
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    long maxId = snapshot.getChildrenCount() + 1;//incrementing the amount of blocked users for this user
                    bdBlock.child(String.valueOf(maxId)).setValue(block_user);//updating table to include new user to blocked table
                    btn.setText("Blocked");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }




}