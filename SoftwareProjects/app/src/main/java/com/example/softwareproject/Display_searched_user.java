package com.example.softwareproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.Gravity;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

public class Display_searched_user extends AppCompatActivity {
    TextView username_view,bio_view;
    AutoCompleteTextView Search_bar;
    ImageView user_image_view;
    ImageButton btn_search;
    Intent intent;
    Search_User_class su;
    String user_username,loggedIn_user;
    long maxId  = 1 ;
    Button followbtn;
    boolean following;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_searched_user);
        intent = getIntent();
        user_username = intent.getStringExtra("username");
        loggedIn_user = intent.getStringExtra("Loggedin_user");
        username_view = (TextView) findViewById(R.id.username_text);
        bio_view = (TextView) findViewById(R.id.bio_text);
        Search_bar = (AutoCompleteTextView) findViewById(R.id.search_bar_input);
        user_image_view = (ImageView) findViewById(R.id.searched_user_image);
        btn_search = (ImageButton) findViewById(R.id.Search_user_button);
        set_user_profile();
        display_posts();
        su = new Search_User_class();
        su.search(loggedIn_user,user_username,Search_bar,btn_search,Display_searched_user.this);
        followbtn = (Button) findViewById(R.id.btnFollow);
        su.getFollowing(loggedIn_user,user_username,followbtn);
        following = su.get_following();
        followbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(following){

                }else{
                    su.follow(loggedIn_user,user_username);
                    followbtn.setText("following");

                }

            }
        });


    }

    public void set_user_profile() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        Query checkUser = reference.orderByChild("username").equalTo(user_username);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    username_view.setText(user_username);
                    String bio = snapshot.child(user_username).child("bio").getValue(String.class);
                    String imageUrl = snapshot.child(user_username).child("mImageUrl").getValue(String.class);
                    Picasso.get().load(imageUrl).into(user_image_view);

                    if (bio.length() >= 1) {
                        bio_view.setText(bio);
                    } else {
                        bio_view.setHint("(Bio will be displayed here)");
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void display_posts(){
        LinearLayout lp = (LinearLayout) findViewById(R.id.scroll_posts);
        lp.setOrientation(LinearLayout.VERTICAL);
        lp.removeAllViews();
        DatabaseReference bd = FirebaseDatabase.getInstance().getReference().child("Posts").child(user_username);
        Query posts =bd.orderByChild(String.valueOf(maxId));
        posts.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Vector<Post> post_data;
                post_data = new Vector<>();
                for(DataSnapshot data:snapshot.getChildren()){
                    String b = data.child("body").getValue(String.class);
                    String t = data.child("time").getValue(String.class);
                    post_data.add(new Post(b,"",t));
                }
                for(int i = post_data.size()-1;i>=0;i--){
                    String post_body = post_data.elementAt(i).getBody();
                    String post_time = post_data.elementAt(i).getTime();

                    TextView body = new TextView(getApplicationContext());
                    TextView time = new TextView(getApplicationContext());
                    LinearLayout post = new LinearLayout(getApplicationContext());

                    post.setOrientation(LinearLayout.VERTICAL);
                    time.setText(post_time);
                    time.setGravity(Gravity.RIGHT);
                    time.setTextSize(15);
                    body.setText("\t"+post_body);
                    body.setTextSize(20);
                    body.setPadding(30,30,30,30);
                    post.addView(time);
                    post.addView(body);
                    post.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.post_layout));
                    post.setPadding(20,30,20,30);
                    lp.addView(post);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}