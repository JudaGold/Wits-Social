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
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Replies extends AppCompatActivity {

    String post_body, URL, post_time, ID, username_post;
    DatabaseReference reference;
    ImageButton btn_home;
    LinearLayout lp;
    String username, account_user;
    Boolean is_searched_user;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.replies_page);
        lp = (LinearLayout) findViewById(R.id.scroll_replies);
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        account_user = intent.getStringExtra("loggedinuser");
        username_post = intent.getStringExtra("username_post");
        ID = intent.getStringExtra("ID");
        post_body = intent.getStringExtra("post_body");
        URL = intent.getStringExtra("URL");
        post_time = intent.getStringExtra("post_time");
        is_searched_user = intent.getBooleanExtra("is_searched_user", false);
        ArrayList<Post> Posts = new ArrayList<>();
        Post post = new Post(ID, username_post, post_body, URL, post_time);
        try {
            post.convertDate();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Posts.add(post);
        display_replies(ID, Posts, is_searched_user);

        btn_home = (ImageButton) findViewById(R.id.btn_home);

        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Replies.this, user_display.class);
                intent.putExtra("username", account_user);
                intent.putExtra("loggedinuser", account_user);
                startActivity(intent);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void display_posts(ArrayList<Post> Posts, Boolean is_searched_user) {
        int counter_reply = 0;

        lp.setOrientation(LinearLayout.VERTICAL);
        lp.setBackgroundColor(Color.parseColor("white"));
        lp.removeAllViews();
        for (Post post : Posts) {
            String post_body = post.getBody();
            String post_time = post.getTime().substring(0, 10);
            String URL = post.getPost_image_url();
            String ID = post.getID();
            String username_post = post.getUsername();

            TextView usernameView = new TextView(Replies.this);
            usernameView.setTextSize(20);

            boolean account_main = false;//checking for logged in user
            if (!is_searched_user) {
                if (username_post.equalsIgnoreCase(username)) {
                    usernameView.setText("Me");
                    account_main = true;
                } else {
                    usernameView.setText(username_post);
                }
            } else {
                if (username_post.equalsIgnoreCase(account_user)) {
                    usernameView.setText("Me");
                    account_main = true;
                } else {
                    usernameView.setText(username_post);
                }
            }
            usernameView.setTextColor(Color.parseColor("#135A71"));
            usernameView.setGravity(Gravity.CENTER);
            lp.addView(usernameView);

            TextView body = createBodyTextView(" " + post_body);
            TextView time = createTimeTextView(post_time);

            LinearLayout postview = createPostLayout();
            postview.addView(time);

            if (URL.length() >= 1) {
                ImageView image = createImageView();
                getImage(URL, image);
                postview.addView(image);
            }

            postview.addView(body);
            if (!account_main) {
                postview.addView(createReplyOption(username_post, post_body, ID));
            }


            postview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Replies.this, Replies.class);
                    intent.putExtra("username", account_user);
                    intent.putExtra("loggedinuser",account_user);
                    intent.putExtra("ID",ID);
                    intent.putExtra("post_body",post_body);
                    intent.putExtra("URL",URL);
                    intent.putExtra("post_time",post_time);
                    intent.putExtra("username_post",username_post);
                    intent.putExtra("is_searched_user",is_searched_user);
                    startActivity(intent);
                }
            });


            lp.addView(postview);

            counter_reply++;

            if (counter_reply == 1) {
                TextView replyTextView = new TextView(Replies.this);
                replyTextView.setTextSize(20);
                replyTextView.setText("Replies");
                replyTextView.setTextColor(Color.parseColor("black"));
                lp.addView(replyTextView);
            }
        }
    }

    public ImageView createImageView() {
        ImageView imageView = new ImageView(Replies.this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1100);
        params.gravity = Gravity.CENTER; //sets the image at the centre
        params.setMargins(0, 40, 0, 50);
        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        return imageView;
    }

    public void getImage(String URL, ImageView image) {
        Glide.with(Replies.this).load(URL).into(image); /*gets image from the internet and adds
                                                                                            it to imageView*/
    }

    public TextView createBodyTextView(String str) {
        TextView body = new TextView(Replies.this);
        body.setText(str);
        body.setTextSize(20);
        body.setPadding(30, 30, 30, 30);
        return body;
    }

    public TextView createTimeTextView(String str) {
        TextView time = new TextView(Replies.this);
        time.setText(str);
        time.setGravity(Gravity.LEFT);
        time.setTextSize(11);
        return time;
    }

    public LinearLayout createPostLayout() {
        LinearLayout post = new LinearLayout(Replies.this);
        post.setOrientation(LinearLayout.VERTICAL);
        post.setBackground(ContextCompat.getDrawable(Replies.this, R.drawable.post_layout));
        post.setPadding(30, 30, 20, 30);
        return post;
    }

    public void display_replies(String postID, ArrayList<Post> Replies, Boolean is_searched_user) {
        reference = FirebaseDatabase.getInstance().getReference("Posts").child(username_post).child(postID).child("Replies");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot data : snapshot.getChildren()) {
                    String id = data.getKey();
                    String b = data.child("body").getValue(String.class);
                    String t = data.child("time").getValue(String.class);
                    String URL = data.child("post_image_url").getValue(String.class);
                    String username = data.child("username").getValue(String.class);
                    Post post = new Post(id, username, b, URL, t);
                    try {
                        post.convertDate();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Replies.add(post);
                }
                Replies.sort(new DateComparator());
                display_posts(Replies, is_searched_user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public TextView createReplyOption(String Reply_to,String post_msg,String uid){//adding a reply text for user to click on to reply to a post
        TextView textView = new TextView(Replies.this);
        textView.setText("reply");
        textView.setTextSize(18);
        textView.setGravity(Gravity.RIGHT);
        textView.setPadding(30,0,20,0);

        textView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                Reply(Reply_to,post_msg,uid);
            }
        });
        return textView;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void Reply(String Reply_to_user, String original_post_msg, String uid){
        AlertDialog.Builder dialogB = new AlertDialog.Builder(Replies.this);
        AlertDialog dialog;
        final View popup_content = getLayoutInflater().inflate(R.layout.pop_up_reply, null);
        TextView popup_header = (TextView) popup_content.findViewById(R.id.reply_header);
        TextView popup_original = (TextView) popup_content.findViewById(R.id.post_replying_to);
        EditText popup_reply_body = (EditText) popup_content.findViewById(R.id.reply_body);
        Button popup_reply_button = (Button) popup_content.findViewById(R.id.btn_reply);
        popup_header.setText("Replying to:\n\t"+Reply_to_user);
        popup_original.setText(original_post_msg);


        dialogB.setView(popup_content);
        dialog = dialogB.create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.popup_dialog_box);
        dialog.show();


        popup_reply_button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                String reply_msg = popup_reply_body.getText().toString();

                Date date = new Date();
                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                String t = (format.format(date));

                DatabaseReference reply_ref = FirebaseDatabase.getInstance().getReference("Posts")
                        .child(Reply_to_user).child(uid).child("Replies");
                reply_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long count = snapshot.getChildrenCount()+1;
                        Post post = new Post(uid,username,reply_msg,"",t);
                        reply_ref.child(String.valueOf(count)).setValue(post);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                DatabaseReference add_reply_post = FirebaseDatabase.getInstance().getReference("Replies")
                        .child(account_user);
                add_reply_post.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long count = snapshot.getChildrenCount()+1;
                        Post post = new Post(String.valueOf(count),Reply_to_user,reply_msg,"",t);
                        add_reply_post.child(String.valueOf(count)).setValue(post);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                dialog.dismiss();
            }
        });
        ;
    }


}