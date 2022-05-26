package com.example.softwareproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    DatabaseReference reference;// this the reference of the Firebase database
    de.hdodenhof.circleimageview.CircleImageView user_image;
    AutoCompleteTextView search_bar;
    String username,looged_in_user;
    Search_User_class su;//creating a instance of the search user class to search for a user
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_display);
        tabLayout = findViewById(R.id.TabLayout);
        viewPager = findViewById(R.id.viewpager);
        tabLayout.setupWithViewPager(viewPager);
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        pagerAdapter.addFragmentTitle(new Fragment_PostFeed(),"Posts");
        pagerAdapter.addFragmentTitle(new fragment_followers(),"Followers");
        pagerAdapter.addFragmentTitle(new fragment_following(),"Following");
        viewPager.setAdapter(pagerAdapter);
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        looged_in_user =  intent.getStringExtra("loggedinuser");
        intent.putExtra("loggedinuser",looged_in_user);

        usernameText = (TextView) findViewById(R.id.username_text);
        bioText = (TextView) findViewById(R.id.bio_text);
        user_image = findViewById(R.id.searched_user_image);
        display_user_information();

        if(username.equalsIgnoreCase(looged_in_user)){
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
            LinearLayout lp_info= findViewById(R.id.lp_info);
            Button btnfollow = new Button(getApplicationContext());
            usernameText.setHeight(36);
            bioText.setHeight(36);
            btnfollow.setHeight(30);


            lp_info.addView(btnfollow);


        }





        search_bar = (AutoCompleteTextView) findViewById(R.id.search_bar_input);
        btn_search_user = (ImageButton) findViewById(R.id.Search_user_button);
        su = new Search_User_class();
        su.search(looged_in_user,username,search_bar,btn_search_user,user_display.this);



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
                    if (bio.length() >= 1) {
                        bioText.setText(bio);
                    } else {
                        bioText.setHint("(Bio will be displayed here)");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}