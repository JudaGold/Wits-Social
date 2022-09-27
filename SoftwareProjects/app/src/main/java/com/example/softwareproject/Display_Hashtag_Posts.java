package com.example.softwareproject;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Pair;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

public class Display_Hashtag_Posts extends AppCompatActivity {
    String ExistingBody, ExistingURL, ExistingTime, ExistingID, ExistingUsername;
    String username;
    String account_user;
    String hashtag;
    LinearLayout lp;
    int counter_reply = 0;

    String post_body, URL, post_time, ID, username_post;
    DatabaseReference reference;
    ImageButton btn_home;
    Boolean is_searched_user;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_hashtag_posts);
        //v =  inflater.inflate(R.layout.fragment__post_feed, container, false);
        Intent intent = Display_Hashtag_Posts.this.getIntent();
        username = intent.getStringExtra("username");
        account_user = intent.getStringExtra("loggedinuser");
        hashtag = intent.getStringExtra("hashtag");
        username_post = intent.getStringExtra("username_post");
        ID = intent.getStringExtra("ID");
        post_body = intent.getStringExtra("post_body");
        URL = intent.getStringExtra("URL");
        post_time = intent.getStringExtra("post_time");

        ArrayList<Post> Posts = new ArrayList<>();
        Post post = new Post(ID, username_post, post_body, URL, post_time);
        try {
            post.convertDate();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Posts.add(post);

        lp = (LinearLayout) findViewById(R.id.post_layout);
      //  display_posts(Posts);

    }


}