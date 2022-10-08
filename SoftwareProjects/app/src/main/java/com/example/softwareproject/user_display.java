package com.example.softwareproject;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

public class user_display extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    TextView usernameText, bioText;// bioText will have the user's bio
    ImageButton btn_search_user;
    Button btnfollow,btnblock;
    DatabaseReference reference;// this the reference of the Firebase database
    de.hdodenhof.circleimageview.CircleImageView user_image;
    AutoCompleteTextView search_bar;
    String username,logged_in_user, fcm_token;
    boolean main_user  = true;
    boolean isfollowing_user = false;
    Search_User_class su;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_display);
        tabLayout = findViewById(R.id.TabLayout);
        viewPager = findViewById(R.id.viewpager);
        tabLayout.setupWithViewPager(viewPager);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        logged_in_user =  intent.getStringExtra("loggedinuser");
        intent.putExtra("loggedinuser",logged_in_user);
        update_FCM_token();

        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        pagerAdapter.addFragmentTitle(new Fragment_PostFeed(),"Posts");//establishing fragment for viewing posts
        pagerAdapter.addFragmentTitle(new fragment_followers(),"Followers");//establishing fragment for viewing user followers
        pagerAdapter.addFragmentTitle(new fragment_following(),"Following");//establishing fragment for viewing user following list

        if(username.equalsIgnoreCase(logged_in_user)){
            pagerAdapter.addFragmentTitle(new fragment_Favourites(),"liked");//adding fragment favourite post to page adapter to view fragment favorites.xml
        }
        viewPager.setAdapter(pagerAdapter);//setting up a viewpager to make swiping across fragments

        reference = FirebaseDatabase.getInstance().getReference("Users");
        usernameText = (TextView) findViewById(R.id.username_text);
        bioText = (TextView) findViewById(R.id.bio_text);
        user_image = findViewById(R.id.searched_user_image);
        btnfollow = new Button(getApplicationContext());
        btnfollow.setText("follow");
        btnblock = new Button(getApplicationContext());
        btnblock.setText("block");
        display_user_information();

        if(username.equalsIgnoreCase(logged_in_user)){
            LinearLayout.LayoutParams Lp = new LinearLayout.LayoutParams(248,52);
            usernameText.setLayoutParams(Lp);
            user_image.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent2 = new Intent(user_display.this, Show_Profile_Details.class);
                    intent2.putExtra("Username", username);
                    startActivity(intent2);
                }
            });
        }
        else{
            main_user = false;
            LinearLayout lp_info= findViewById(R.id.lp_info);
            LinearLayout lv = new LinearLayout(getApplicationContext());
            lv.setOrientation(LinearLayout.HORIZONTAL);
            lv.addView(btnfollow);
            lv.addView(btnblock);
            lp_info.addView(lv);
            lp_info.removeView(bioText);
            is_following();//checking if current user is following this account
            is_blocked();//checking if current user is blocking this account
        }
        search_bar = (AutoCompleteTextView) findViewById(R.id.search_bar_input);
        btn_search_user = (ImageButton) findViewById(R.id.Search_user_button);
        su = new Search_User_class();
        su.search(logged_in_user,username,search_bar,btn_search_user,user_display.this);

        btnfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update_social();
            }
        });

        btnblock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Search_User_class su = new Search_User_class();//instantiating class to call for blocking and unblocking a user
                su.block_user(logged_in_user,username,btnblock);//calling blocking function to block user
            }
        });


    }

    public void display_user_information() {
        Query checkUser = reference.orderByChild("username").equalTo(username);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    usernameText.setText(username);
                        String bio = snapshot.child(username).child("bio").getValue(String.class);
                        String imageUrl = snapshot.child(username).child("mImageUrl").getValue(String.class);
                        try {
                            Picasso.get().load(imageUrl).into(user_image);
                        }catch(Exception c){}
                    if(main_user){
                        if (bio.length() >= 1) {
                            bioText.setText(bio);
                        } else {
                            bioText.setHint("(Bio will be displayed here)");
                        }
                    }
                    else{
                        if (bio.length() >= 1) {
                            usernameText.append(":\t"+bio);
                        } else {
                            usernameText.append(":\t(Bio will be displayed here)");
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void is_following(){//checking is user is being followed by current account

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("social").child(logged_in_user).child("following");

       ref.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               boolean found = false;
               if(snapshot.exists()){
                   for(DataSnapshot data:snapshot.getChildren()){
                       if(data.getValue().equals(username)) {
                           btnfollow.setText("Following");
                           found = true;
                           break;
                       }

                       if(!found){
                           btnfollow.setText("Follow");
                       }

                   }
               }
           }
           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });
    }
    void update_social(){//checking if user wants to follow or unfollow
        Search_User_class search_user_class = new Search_User_class();
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("social").child(logged_in_user).child("following");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean found = false;
                for(DataSnapshot data:snapshot.getChildren()){
                    if(data.getValue().equals(username)){
                        btnfollow.setText("Follow");
                        search_user_class.unfollow(logged_in_user,username);
                        found = true;
                        break;
                    }
                }
                if(!found){
                    btnfollow.setText("Following");
                    search_user_class.follow(logged_in_user,username, fcm_token);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void is_blocked(){//function to check if current user is currently blocked
        DatabaseReference blockdb = FirebaseDatabase.getInstance().getReference("social").child(logged_in_user).child("Blocking");
        blockdb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for(DataSnapshot ds :snapshot.getChildren()){//checking if user is on the list
                        if(ds.getValue().equals(username)){
                            btnblock.setText("blocked");//setting button test to notify usr is blocked
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void update_FCM_token()
    {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        fcm_token = task.getResult();

                        reference.child(username).child("fcm_token").setValue(fcm_token);
                    }
                });
    }

    void unblock_user(){

    }
}

