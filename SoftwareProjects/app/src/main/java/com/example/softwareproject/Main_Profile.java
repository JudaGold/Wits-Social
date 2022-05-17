package com.example.softwareproject;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;

public class Main_Profile extends AppCompatActivity {
    TextView usernameText, bioText;
    EditText popup_post_body,popup_post_image;
    Button popup_add_post;
    ImageButton btnadd_post;
    DatabaseReference reference;
    ImageView user_image;
    long maxId  = 1 ;
    String username;
    ArrayList<String> following = new ArrayList<String>();
    Hashtable<String, Integer> username_colours = new Hashtable<String, Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_profile);
        Intent intent = getIntent();
        usernameText = (TextView) findViewById(R.id.username2);
        bioText = (TextView) findViewById(R.id.user_bio);
        username = intent.getStringExtra("username");
        user_image = (ImageView) findViewById(R.id.user_image);
        set_user_profile();
        getFollowing();
        //display_posts();
        btnadd_post = (ImageButton) findViewById(R.id.btn_add_post);
        btnadd_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_post();
            }
        });

        user_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(Main_Profile.this,Show_Profile_Details.class);
                intent2.putExtra("Username",username);
                startActivity(intent2);
            }
        });

    }

    public void set_user_profile( ) {
        reference = FirebaseDatabase.getInstance().getReference("Users");
        Query checkUser = reference.orderByChild("username").equalTo(username);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    usernameText.setText(username);
                    String bio = snapshot.child(username).child("bio").getValue(String.class);
                    String imageUrl = snapshot.child(username).child("mImageUrl").getValue(String.class);
                    Picasso.get().load(imageUrl).into(user_image);

                    if(bio.length()>=1){
                        bioText.setText(bio);
                    }
                    else{
                        bioText.setHint("(Bio will be displayed here)");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void add_post( ){
        AlertDialog.Builder dialogB = new AlertDialog.Builder(this);
        AlertDialog dialog;
        final View popup_content = getLayoutInflater().inflate(R.layout.popup_post,null);
        popup_post_body= (EditText) popup_content.findViewById(R.id.post_body);
        popup_post_image =(EditText) popup_content.findViewById(R.id.post_image);
        popup_add_post = (Button) popup_content.findViewById(R.id.btn_post);
        dialogB.setView(popup_content);
        dialog = dialogB.create();
        dialog.show();

        popup_add_post.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

                 try {
                    Post post;
                    String body = popup_post_body.getText().toString();
                    //String image_url = popup_post_image.getText().toString()
                     Date date = new Date();
                     SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                     String t = (format.format(date)).toString();

                     post = new Post(body,"",t);
                     reference = FirebaseDatabase.getInstance().getReference("Posts").child(username);

                     reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()) {
                                    maxId = (snapshot.getChildrenCount()) + 1;
                                }
                            reference.child(String.valueOf(maxId)).setValue(post);
                            dialog.dismiss();
                            display_posts(following, username_colours);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }catch(Exception e){
                }
            }
        });
    }

    public void display_posts(ArrayList<String> following, Hashtable<String, Integer> username_colours){
        LinearLayout lp = (LinearLayout) findViewById(R.id.scroll_posts);
        lp.setOrientation(LinearLayout.VERTICAL);
        lp.setBackgroundColor(Color.LTGRAY);
        lp.removeAllViews();
        reference = FirebaseDatabase.getInstance().getReference().child("Posts").child(username);
        Query posts = reference.orderByChild(String.valueOf(maxId));
        posts.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Vector<Post>post_data;
                post_data = new Vector<Post>();
                for(DataSnapshot data:snapshot.getChildren()){
                    String b = data.child("body").getValue(String.class);
                    String t = data.child("time").getValue(String.class);
                    post_data.add(new Post(b,"",t));
                }
                for(int i = post_data.size()-1;i>=0;i--){
                    String post_body = post_data.elementAt(i).getBody();
                    String post_time = post_data.elementAt(i).getTime();

                    TextView username = new TextView(getApplicationContext());
                    username.setText("Me:");
                    username.setTextSize(20);
                    username.setTextColor(Color.parseColor("#ff0099cc"));
                    lp.addView(username);

                    TextView post = new TextView(getApplicationContext());
                    post.setTextSize(20);
                    post.setPadding(30,30,30,30);
                    post.setText(post_body+"\n"+post_time);
                    post.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.post_layout));
                    lp.addView(post);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        for (String usernames : following) {
            reference = FirebaseDatabase.getInstance().getReference().child("Posts").child(usernames);
            Query following_posts = reference.orderByChild(String.valueOf(maxId));
            following_posts.addListenerForSingleValueEvent(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Vector<Post>post_data;
                    post_data = new Vector<Post>();
                    for(DataSnapshot data:snapshot.getChildren()){
                        String b = data.child("body").getValue(String.class);
                        String t = data.child("time").getValue(String.class);
                        post_data.add(new Post(b,"",t));
                    }
                    for(int i = post_data.size()-1;i>=0;i--){
                        String post_body = post_data.elementAt(i).getBody();
                        String post_time = post_data.elementAt(i).getTime();

                        TextView username = new TextView(getApplicationContext());
                        username.setText(usernames + ":");
                        username.setTextSize(20);
                        username.setTextColor(username_colours.get(usernames));
                        lp.addView(username);

                        TextView post = new TextView(getApplicationContext());
                        post.setTextSize(20);
                        post.setPadding(30,30,30,30);
                        post.setText(post_body+"\n"+post_time);
                        post.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.post_layout));
                        lp.addView(post);

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public void getFollowing()
    {
        reference = FirebaseDatabase.getInstance().getReference("Following").child(username);

        reference.addListenerForSingleValueEvent(new ValueEventListener(){

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data:snapshot.getChildren())
                {
                    String following_username = data.getValue(String.class);
                    following.add(following_username);
                    Random rnd = new Random();
                    int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                    username_colours.put(following_username, color);
                }

                display_posts(following, username_colours);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}