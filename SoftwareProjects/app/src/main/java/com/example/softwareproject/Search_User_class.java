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

public class Search_User_class {//class to search for users
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
                    if(!text.isEmpty()){
                        if (text.charAt(0) == '#')//if searching for a hashtag
                        {
                            if (search_user_hashtags(text)) {
                                text = text.substring(1);
                                intent = new Intent(activity, Display_Hashtag_Posts.class);//show all posts with that hashtag
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
                            if (search_user_hashtags(text)) {//if searching for users
                                intent = new Intent(activity, user_display.class);//go take to that users page
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

                }
            });


    }

    private void getUsersHashtags(String My_username, String loggedin_user) {//getting all hashtags/users for search
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        Query userlist = reference.orderByChild("username");
        users_hashtags = new ArrayList<>();//an array to store all the users on database
        userlist.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {//getting all users from the database for search
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
            public void onDataChange(@NonNull DataSnapshot snapshot) {//getting all hashtags from the database for search
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

    private boolean search_user_hashtags(String user) {//checking if searched user/hashtag is valid
        for (String item : users_hashtags) {
            if (user.equalsIgnoreCase(item)) {
                return true;
            }
        }
        return false;
    }

    public void follow(String main,String user, String fcm_token){//set that current user now follows searched user when they click follow
        follow_user(main,user);
        setFollowing(main,user, fcm_token);

    }
    private void follow_user(String user,String searched_user){//way for user to foolow another user
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("social").child(user).child("following");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long maxId = snapshot.getChildrenCount()+1;
                if(snapshot.child(searched_user).exists()){

                }
                else{
                    ref.push().setValue(searched_user);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setFollowing(String main_user,String user, String fcm_token){//way to allow user to follow another user
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("social").child(user).child("followers");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long maxId = snapshot.getChildrenCount()+1;
                if(snapshot.child(main_user).exists()){//if already following do nothing

                }
                else{
                    ref.push().setValue(main_user);
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
                if(snapshot.child(fcm_token).exists()){//add that current user gets a notification from searched user(if done already do nothing)

                }
                else{
                    ref2.child(String.valueOf(maxId)).setValue(fcm_token);//add that current user gets a notification from searched user
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void unfollow(String main,String user){//way to unfollow user
        set_unfollow(main,user);
        set_unfollower(main,user);
    }

    private void set_unfollow(String main,String user){//remove that current user is no longer following another user who he is trying to unfollow
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("social").child(main).child("following");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
              for(DataSnapshot data: snapshot.getChildren()){
                  if(data.getValue(String.class).equalsIgnoreCase(user)){
                      data.getRef().removeValue();//remove the follow from the database
                      break;

                  }
              }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void set_unfollower(String main,String user){//remove follower from database
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


    public void block_user(String curr_user, String block_user, Button btn,Button btn2){//function to block a user
        DatabaseReference bdBlock = FirebaseDatabase.getInstance()
                .getReference("social").child(curr_user).child("Blocking");
        String buttonText = btn.getText().toString();
        if(buttonText.equalsIgnoreCase("Blocked")){
            bdBlock.addListenerForSingleValueEvent(new ValueEventListener() {//setting a new listener to access the database
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot data: snapshot.getChildren()) {
                        if (data.getValue(String.class).equalsIgnoreCase(block_user)) {
                            btn2.setVisibility(View.VISIBLE);
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
//                    bdBlock.push().setValue(block_user);//updating table to include new user to blocked table
                    long maxId = snapshot.getChildrenCount()+1;
                    if(snapshot.child(block_user).exists()){

                    }
                    else{
                        bdBlock.child(String.valueOf(maxId)).setValue(block_user);
                    }
                    btn.setText("Blocked");
                    unfollow(curr_user,block_user);//sets user to unfollow the user that blocked them
                    btn2.setVisibility(View.INVISIBLE);
                    btn2.setText("follow");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }




}
