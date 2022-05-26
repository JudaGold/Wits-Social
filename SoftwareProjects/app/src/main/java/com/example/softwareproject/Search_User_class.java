package com.example.softwareproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

@Generated
public class Search_User_class {
    private static ArrayList<String> users;
    private static ArrayList<String>followers;
    boolean following = false;
    Intent intent;

    public void search(String loggedin_user, String My_username, AutoCompleteTextView ACT, ImageButton btn, Activity activity) {
        getUsers(My_username, loggedin_user);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, users);
        ACT.setThreshold(1);
        ACT.setAdapter(adapter);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String user = ACT.getText().toString();
                if (search_user(user)) {
                    intent = new Intent(activity, user_display.class);
                    intent.putExtra("username", user);
                    intent.putExtra("loggedinuser",loggedin_user);
                    activity.startActivity(intent);
                    activity.finish();
                } else {
                    ACT.setText("");
                    ACT.setHint("no user " + user + " found");
                }

            }
        });
    }

    private void getUsers(String My_username, String loggedin_user) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        Query userlist = reference.orderByChild("username");
        users = new ArrayList<>();
        userlist.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    String username = data.child("username").getValue(String.class);

                    if (!username.equalsIgnoreCase(My_username)) {
                        if (!username.equalsIgnoreCase(loggedin_user)) {
                            users.add(username);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private boolean search_user(String user) {
        for (String item : users) {
            if (user.equalsIgnoreCase(item)) {
                return true;
            }
        }
        return false;
    }

    public void getFollowing(String LoggedIn_user, String SearchUser, Button btn_follow) {
        followers = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("Following").child(SearchUser);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean following = false;
                for(DataSnapshot data:snapshot.getChildren()){
                    String following_username = data.getValue(String.class);
                    followers.add(following_username);
                    if(following_username.equalsIgnoreCase(SearchUser)){
                        following = true;
                    }
                }

                if(following){
                    btn_follow.setText("following");
                }
                else{
                    btn_follow.setText("follow");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void follow(String LoggedIn_user,String SearchUser){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Following").child(LoggedIn_user);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long maxId = snapshot.getChildrenCount()+1;
                if(snapshot.exists()){
                    reference.child(String.valueOf(maxId)).setValue(SearchUser);
                }
                else{
                    reference.child(String.valueOf(maxId)).setValue(SearchUser);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public boolean get_following(){
        return this.following;
    }
}