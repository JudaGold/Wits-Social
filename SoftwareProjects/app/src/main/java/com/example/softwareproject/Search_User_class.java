package com.example.softwareproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

public class Search_User_class {
    private static ArrayList<String>users;
    Intent intent;

    public void search(String My_username,AutoCompleteTextView ACT, ImageButton btn, Activity activity) {
        getUsers(My_username);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, users);
        int limit = ACT.getText().toString().length();
        ACT.setThreshold(1);
        ACT.setAdapter(adapter);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String user = ACT.getText().toString();
                if(search_user(user)){
                    intent = new Intent(activity,Display_searched_user.class);
                    intent.putExtra("username",user);
                    activity.startActivity(intent);
                }
                else{
                    ACT.setText("");
                    ACT.setHint("no user "+user+" found");
                }

            }
        });
    }
    private void getUsers(String My_username){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        Query userlist = reference.orderByChild("username");
        users = new ArrayList<>();
        userlist.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    String username = data.child("username").getValue(String.class);

                    if(!username.equalsIgnoreCase(My_username))
                        users.add(username);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private boolean search_user(String user){
        for(String item:users){
            if(user.equalsIgnoreCase(item)){
                return true;
            }
        }
        return false;
    }

}