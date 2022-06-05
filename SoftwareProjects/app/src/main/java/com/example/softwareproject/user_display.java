package com.example.softwareproject;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class user_display extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    TextView usernameText, bioText;// bioText will have the user's bio
    ImageButton btn_search_user;
    Button  btnfollow;
    DatabaseReference reference;// this the reference of the Firebase database
    de.hdodenhof.circleimageview.CircleImageView user_image;
    AutoCompleteTextView search_bar;
    String username,logged_in_user;
    Search_User_class su;//creating a instance of the search user class to search for a user
    boolean main_user  = true;
    boolean following = false;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_display);
        tabLayout = findViewById(R.id.TabLayout);
        viewPager = findViewById(R.id.viewpager);
        tabLayout.setupWithViewPager(viewPager);
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        pagerAdapter.addFragmentTitle(new Fragment_PostFeed(),"Posts");//establishing fragment for viewing posts
        pagerAdapter.addFragmentTitle(new fragment_followers(),"Followers");//establishing fragment for viewing user followers
        pagerAdapter.addFragmentTitle(new fragment_following(),"Following");//establishing fragment for viewing user following list
        viewPager.setAdapter(pagerAdapter);//setting up a viewpager to make swiping across fragments
        Intent intent = getIntent();

        username = intent.getStringExtra("username");
        logged_in_user =  intent.getStringExtra("loggedinuser");
        intent.putExtra("loggedinuser",logged_in_user);

        usernameText = (TextView) findViewById(R.id.username_text);
        bioText = (TextView) findViewById(R.id.bio_text);
        user_image = findViewById(R.id.searched_user_image);
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
            btnfollow = new Button(getApplicationContext());
            lp_info.addView(btnfollow);
            lp_info.removeView(bioText);
            is_following();
        }
        search_bar = (AutoCompleteTextView) findViewById(R.id.search_bar_input);
        btn_search_user = (ImageButton) findViewById(R.id.Search_user_button);
        su = new Search_User_class();
        su.search(logged_in_user,username,search_bar,btn_search_user,user_display.this);
    }

    public void display_user_information() {
        reference = FirebaseDatabase.getInstance().getReference("Users");
        Query checkUser = reference.orderByChild("username").equalTo(username);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    usernameText.setText(username);
                        String bio = snapshot.child(username).child("bio").getValue(String.class);
                        String imageUrl = snapshot.child(username).child("mImageUrl").getValue(String.class);
                        Picasso.get().load(imageUrl).into(user_image);
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
    private void is_following(){
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("social").child(logged_in_user)
                .child("following");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot data:snapshot.getChildren()){
                        if (data.getValue(String.class).equalsIgnoreCase(username)){
                            following = true;
                            break;
                        }
                    }
                }

                if(following){
                    btnfollow.setText("Following");
                    btnfollow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            su.unfollow(logged_in_user,username);
                            following = false;
                            btnfollow.setText("Follow");
                        }
                    });
                }
                else{
                    btnfollow.setText("Follow");
                    btnfollow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            su.follow(logged_in_user,username);
                            btnfollow.setText("Following");
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void update_FCM_token(String token)
    {

    }
}