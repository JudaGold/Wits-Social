package com.example.softwareproject;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AutoCompleteTextView;
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

import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Random;
import java.util.logging.Handler;

@Generated
public class Main_Profile extends AppCompatActivity {
    TextView usernameText, bioText;// bioText will have the user's bio
    EditText popup_post_body, popup_post_image;
    AutoCompleteTextView search_bar;
    Button popup_add_post;
    ImageButton btnadd_post,btn_search_user;
    DatabaseReference reference;// this the reference of the Firebase database
    ImageView user_image;// this will have be image on the main feed page
    long maxId = 1;
    String username;
    SwipeRefreshLayout swipeRefreshLayout;
    LinearLayout lp;
    ArrayList<String> all_usernames = new ArrayList<>();/* this will have the user's username
                                                           and the usernames of the users, the
                                                           user is following*/
    Hashtable<String, Integer> username_colours = new Hashtable<>();/* this will have the unique
                                                                       colour of each user*/
    Search_User_class su;//creating a instance of the search user class to search for a user


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_profile);
        Intent intent = getIntent();
        usernameText = (TextView) findViewById(R.id.username_text);
        bioText = (TextView) findViewById(R.id.bio_text);
        username = intent.getStringExtra("username");
        user_image = (ImageView) findViewById(R.id.searched_user_image);
        all_usernames.add(username);
        set_user_profile();
        getFollowing();
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
                Intent intent2 = new Intent(Main_Profile.this, Show_Profile_Details.class);
                intent2.putExtra("Username", username);
                startActivity(intent2);
            }
        });
       search_bar = (AutoCompleteTextView) findViewById(R.id.search_bar_input);
       btn_search_user = (ImageButton) findViewById(R.id.Search_user_button);
       su = new Search_User_class();
       su.search(username,username,search_bar,btn_search_user,Main_Profile.this);

       swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiper);
       swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
           @RequiresApi(api = Build.VERSION_CODES.O)
           @Override
           public void onRefresh() {
            lp.removeAllViews();
            getFollowing();
            //fetchPosts(all_usernames, username_colours);
            swipeRefreshLayout.setRefreshing(false);
           }
       });



    }

    public void set_user_profile() {
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

    public void add_post() {
        AlertDialog.Builder dialogB = new AlertDialog.Builder(this);
        AlertDialog dialog;
        final View popup_content = getLayoutInflater().inflate(R.layout.popup_post, null);
        popup_post_body = (EditText) popup_content.findViewById(R.id.post_body);
        popup_post_image = (EditText) popup_content.findViewById(R.id.post_image);
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
                    String t = (format.format(date));

                    post = new Post(body, "", t);
                    reference = FirebaseDatabase.getInstance().getReference("Posts").child(username);
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                maxId = (snapshot.getChildrenCount()) + 1;
                            }
                            reference.child(String.valueOf(maxId)).setValue(post);
                            dialog.dismiss();
                            fetchPosts(all_usernames, username_colours);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } catch (Exception e) {
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void display_posts(ArrayList<Post> Posts, Hashtable<String, Integer> username_colours) {
         lp = (LinearLayout) findViewById(R.id.scroll_posts);
        lp.setOrientation(LinearLayout.VERTICAL);
        lp.setBackgroundColor(Color.LTGRAY);
        lp.removeAllViews();
        for (Post post : Posts) {
            String post_body = post.getBody();
            String post_time = post.getTime();
            String username_post = post.getUsername();

            TextView usernameView = new TextView(getApplicationContext());
            usernameView.setTextSize(20);

            if (username_post.equalsIgnoreCase(username))
            {
                usernameView.setText("Me:");
                usernameView.setTextColor(Color.parseColor("#ff0099cc"));
            }
            else
            {
                usernameView.setText(username_post + ":");
                usernameView.setTextColor(username_colours.get(username_post));
            }

            lp.addView(usernameView);

            TextView body = new TextView(getApplicationContext());
            TextView time = new TextView(getApplicationContext());

            LinearLayout postview = new LinearLayout(getApplicationContext());
            postview.setOrientation(LinearLayout.VERTICAL);
            time.setText(post_time);
            time.setGravity(Gravity.RIGHT);
            time.setTextSize(15);
            body.setText(" "+post_body);
            body.setTextSize(20);
            body.setPadding(30,30,30,30);
            postview.addView(time);
            postview.addView(body);
            postview.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.post_layout));
            postview.setPadding(20,30,20,30);
            lp.addView(postview);


        }
    }

    public void getFollowing() {
        reference = FirebaseDatabase.getInstance().getReference("Following").child(username);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    String following_username = data.getValue(String.class);
                    all_usernames.add(following_username);
                    Random rnd = new Random();
                    int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                    username_colours.put(following_username, color);
                }
                fetchPosts(all_usernames, username_colours);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void fetchPosts(ArrayList<String> following, Hashtable<String, Integer> username_colours) {
        ArrayList<Post> Posts = new ArrayList<>();

        for (String usernames : following) {
            reference = FirebaseDatabase.getInstance().getReference().child("Posts").child(usernames);
            Query following_posts = reference.orderByChild(String.valueOf(maxId));
            following_posts.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        String b = data.child("body").getValue(String.class);
                        String t = data.child("time").getValue(String.class);
                        Post post = new Post(b, "", t);
                        post.setUsername(usernames);
                        try {
                            post.convertDate();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Posts.add(post);
                    }
                    Posts.sort(new DateComparator());
                    display_posts(Posts, username_colours);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }


}