package com.example.softwareproject;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Data_Collection {

    public User user;

    public User User_Collection(String username) {
        user = new User();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        Query getUserInfo = reference.orderByChild("username").equalTo(username);
        getUserInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    user.setUsername(username);
                    user.setBio(snapshot.child(username).child("bio").getValue(String.class));
                    user.setName(snapshot.child(username).child("name").getValue(String.class));
                    user.setPhoneNumber(snapshot.child(username).child("phoneNumber").getValue(String.class));
                    user.setEmail(snapshot.child(username).child("email").getValue(String.class));
                    user.setmImageUrl(snapshot.child(username).child("mImageUrl").getValue(String.class));
                }
                else
                {
                    user.setUsername("Nonexistent");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return user;
    }

    public boolean userExists(String username)
    {
        if (username.equals("Nonexistent"))
        {
            return false;
        }

        return true;
    }
}
