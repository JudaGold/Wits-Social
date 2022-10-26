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
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    // Declarations of variables
    String username; // Username of the logged in user
    String account_user; // Placeholder of the user's username
    String hashtag; // The post's hashtag
    LinearLayout lp; // Layout of the screen
    DatabaseReference reference; // this the reference of the Firebase database
    long maxId = 1; // This is the default value for the first record's ID in a table has in the database
    UI_Views views = new UI_Views(); /* This is an object to the class UI_Views which is used to
                                        create views in the layout of the screen*/

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialisation of variables
        setContentView(R.layout.activity_display_hashtag_posts);
        Intent intent = Display_Hashtag_Posts.this.getIntent();
        username = intent.getStringExtra("username"); // Fetching the user's username
        account_user = intent.getStringExtra("username"); // Fetching the user's username
        hashtag = intent.getStringExtra("hashtag"); // Fetching the clicked hashtag
        lp = (LinearLayout) findViewById(R.id.post_layout);
        ImageButton btn_home = (ImageButton) findViewById(R.id.btnHome);

        fetch_hashtag_posts(hashtag); /* Calls the method that displays the posts with the clicked
                                         hashtag on to the screen*/

        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Display_Hashtag_Posts.this, user_display.class);
                intent.putExtra("username", account_user);
                intent.putExtra("loggedinuser", account_user);
                Display_Hashtag_Posts.this.startActivity(intent);
            }
        });

    }

    // This method fetches the posts with the hashtag from the database
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void fetch_hashtag_posts(String hashtag) {
        ArrayList<Post> Posts = new ArrayList<Post>();
        reference = FirebaseDatabase.getInstance().getReference("Hashtags").child(hashtag.trim());
        Query hashtag_posts = reference.orderByChild(String.valueOf(maxId));
        hashtag_posts.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        try {
                            String id = data.getKey();
                            String b = data.child("body").getValue(String.class);
                            String t = data.child("time").getValue(String.class);
                            String URL = data.child("post_image_url").getValue(String.class);
                            String num_of_replies = data.child("Replies").getChildrenCount() + "";
                            String username = data.child("username").getValue(String.class);
                            Post post = new Post(id, username, b, URL, t);
                            post.setNum_of_replies(num_of_replies);
                            try {
                                post.convertDate();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Posts.add(post);
                        } catch (Exception e) {
                        }

                    }
                    Posts.sort(new DateComparator());
                    display_posts(Posts, false, false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // This method displays the posts withe hashtag onto the screen
    @RequiresApi(api = Build.VERSION_CODES.O)
    void display_posts(ArrayList<Post> Posts, Boolean Edits, Boolean is_searched_user) {


        if (Posts.size() > 0) {

            lp.setOrientation(LinearLayout.VERTICAL);
            lp.removeAllViews();
            for (Post post : Posts) {
                try {
                    String uid = post.getID();
                    String post_body = post.getBody();
                    String post_time = post.getTime().substring(0, 10);
                    String URL = post.getPost_image_url();
                    String ID = post.getID();
                    String username_post = post.getUsername();
                    String num_of_replies = post.getNum_of_replies();


                    TextView usernameView;

                    boolean account_main = false;//checking for logged in user
                    if (!is_searched_user) {
                        if (username_post.equalsIgnoreCase(username)) {
                            usernameView = views.createUsernameTextView(Display_Hashtag_Posts.this, "Me");
                            account_main = true;
                        } else {
                            usernameView = views.createUsernameTextView(Display_Hashtag_Posts.this, username_post);
                        }
                    } else {
                        if (username_post.equalsIgnoreCase(account_user)) {
                            usernameView = views.createUsernameTextView(Display_Hashtag_Posts.this, "Me");
                            account_main = true;
                        } else {
                            usernameView = views.createUsernameTextView(Display_Hashtag_Posts.this, username_post);
                        }
                    }

                    if (username_post.length() > 2) {
                        if (username_post.substring(0, 2).equalsIgnoreCase("Me")) {
                            account_main = true;
                        }
                    }

                    LinearLayout hl = views.createHorizontalLayout(Display_Hashtag_Posts.this);
                    hl.setGravity(Gravity.NO_GRAVITY);
                    hl.addView(usernameView);

                    String new_post_body = post_body + " ";
                    SpannableString spanString = processHashtag(new_post_body, uid, URL, post_time, username_post);
                    TextView body = createBodyTextViewHashtag(spanString);


                    TextView time = views.createTimeTextView(Display_Hashtag_Posts.this, post_time);

                    LinearLayout postview = views.createPostLayout(Display_Hashtag_Posts.this);
                    hl.addView(time);
                    postview.addView(hl);

                    if (URL.length() >= 1) {
                        ImageView image = createImageView();
                        getImage(URL, image);
                        postview.addView(image);
                    }

                    ToggleButton favouritesButton = createFavouriteToggleButton(username, username_post, ID);
                    LinearLayout horizontalLayout = views.createHorizontalLayout(Display_Hashtag_Posts.this);
                    postview.addView(body);
                    if (!num_of_replies.equalsIgnoreCase("")) {
                        TextView replies = createNumOfReplies(num_of_replies);
                        horizontalLayout.addView(replies);
                    }

                    if (!account_main) {
                        horizontalLayout.addView(favouritesButton);
                        horizontalLayout.addView(createReplyOption(username_post, post_body, uid));
                    }

                    if (!Edits) {
                        postview.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Display_Hashtag_Posts.this, Replies.class);
                                intent.putExtra("username", account_user);
                                intent.putExtra("loggedinuser", account_user);
                                intent.putExtra("ID", ID);
                                intent.putExtra("post_body", post_body);
                                intent.putExtra("URL", URL);
                                intent.putExtra("post_time", post_time);
                                intent.putExtra("username_post", username_post);
                                intent.putExtra("is_searched_user", false);
                                Display_Hashtag_Posts.this.startActivity(intent);
                                Display_Hashtag_Posts.this.finish();
                            }
                        });
                    }

                    postview.addView(horizontalLayout);
                    lp.addView(views.addSpace(Display_Hashtag_Posts.this));
                    lp.addView(postview);

                } catch (Exception e) {
                }
            }
        }
    }

    // This displays an image onto the screen
    public ImageView createImageView() {
        ImageView imageView = new ImageView(Display_Hashtag_Posts.this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1100);
        params.gravity = Gravity.LEFT; //sets the image at the centre
        params.setMargins(0, 40, 0, 50);
        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        return imageView;
    }

    // Gets image from the internet and adds it to imageView
    public void getImage(String URL, ImageView image) {
        Glide.with(Display_Hashtag_Posts.this).load(URL).into(image);
    }

    // Shows text with a hashtag
    public TextView createBodyTextViewHashtag(SpannableString str) {
        TextView body = new TextView(Display_Hashtag_Posts.this);
        body.setText(str);
        body.setTextSize(20);
        body.setMovementMethod(LinkMovementMethod.getInstance());
        body.setTextColor(Color.parseColor("white"));
        body.setPadding(30, 30, 30, 30);
        body.setMovementMethod(LinkMovementMethod.getInstance());
        return body;
    }

    // Creates a reply button onto a post
    public TextView createReplyOption(String Reply_to, String post_msg, String uid) {//adding a reply text for user to click on to reply to a post
        TextView textView = new TextView(Display_Hashtag_Posts.this);
        textView.setText("reply");
        textView.setTextSize(18);
        textView.setGravity(Gravity.RIGHT);
        textView.setPadding(30, 0, 20, 0);
        textView.setTextColor(Color.parseColor("white"));

        textView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                Reply(Reply_to, post_msg, uid);
            }
        });
        return textView;
    }

    // Shows the number of replies for a post
    public TextView createNumOfReplies(String num_of_replies) {
        TextView textView = new TextView(Display_Hashtag_Posts.this);
        textView.setText(num_of_replies);
        textView.setTextSize(15);
        textView.setGravity(Gravity.RIGHT);
        textView.setPadding(30, 0, 20, 0);
        textView.setTextColor(Color.parseColor("white"));
        textView.setBackground(ContextCompat.getDrawable(Display_Hashtag_Posts.this, R.drawable.ic_round_chat_bubble_outline_24));

        return textView;
    }

    // Creates a button to favourite a post
    public ToggleButton createFavouriteToggleButton(String user, String userPost, String ID) {
        ToggleButton toggleButton = new ToggleButton(Display_Hashtag_Posts.this);
        toggleButton.setText(""); //removes all text from the toggle button so that only the heart shows
        toggleButton.setTextOn("");
        toggleButton.setTextOff("");
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(80, 80);
        toggleButton.setLayoutParams(params);
        toggleButton.setPadding(30, 0, 200, 0);
        toggleButton.setBackgroundResource(R.drawable.toggle_selector);
        toggleButton.setClickable(true);

        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (toggleButton.isChecked()) {
                    Toast.makeText(Display_Hashtag_Posts.this, "added to favourites", Toast.LENGTH_SHORT).show();
                    addFavourite(user, userPost, ID);
                } else {
                    Toast.makeText(Display_Hashtag_Posts.this, "removed from favourites", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return toggleButton;
    }

    // This method saves a reply made byt the user to the database
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void Reply(String Reply_to_user, String original_post_msg, String uid) {
        AlertDialog.Builder dialogB = new AlertDialog.Builder(Display_Hashtag_Posts.this);
        AlertDialog dialog;
        final View popup_content = getLayoutInflater().inflate(R.layout.pop_up_reply, null);
        TextView popup_header = (TextView) popup_content.findViewById(R.id.reply_header);
        TextView popup_original = (TextView) popup_content.findViewById(R.id.post_replying_to);
        popup_original.setTextSize(11);
        EditText popup_reply_body = (EditText) popup_content.findViewById(R.id.reply_body);
        Button popup_reply_button = (Button) popup_content.findViewById(R.id.btn_reply);
        popup_header.setText("Replying to:\n\t" + Reply_to_user);
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
                        long count = snapshot.getChildrenCount() + 1;
                        Post post = new Post(uid, account_user, reply_msg, "", t);
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
                        long count = snapshot.getChildrenCount() + 1;
                        Post post = new Post(String.valueOf(count), Reply_to_user, reply_msg, "", t);
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

    // This saves the liked post to the database
    public void addFavourite(String user, String userPost, String postID) {
        DatabaseReference favRef = FirebaseDatabase.getInstance().getReference("FavouritePosts").child(user).child(userPost);
        favRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                favRef.child("ID").setValue(postID);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }

    // This displays a hashtag in a post
    public SpannableString processHashtag(String body, String ID, String URL, String post_time, String username_post) {
        int index = body.indexOf("#"); // looks for the position of # in string
        int endIndex = body.indexOf(" ", index);
        String str = body.substring(index, endIndex);
        SpannableString spanString = new SpannableString(body);
        ForegroundColorSpan fcsCyan = new ForegroundColorSpan(Color.CYAN);

        spanString.setSpan(fcsCyan, index, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spanString;
    }


}