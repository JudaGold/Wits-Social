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
    public void fetch_hashtag_posts(String hashtag) {//function to fetch all the post witha hash tag
        ArrayList<Post> Posts = new ArrayList<Post>();//setting up a new array to store posts
        reference = FirebaseDatabase.getInstance().getReference("Hashtags").child(hashtag.trim());//firebase ref
        Query hashtag_posts = reference.orderByChild(String.valueOf(maxId));//firebase query for all the hashtags
        hashtag_posts.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        try {
                            String id = data.getKey();//getting id from snapshot
                            String b = data.child("body").getValue(String.class);//getting body from snapshot
                            String t = data.child("time").getValue(String.class);//getting time from snapshot
                            String URL = data.child("post_image_url").getValue(String.class);//getting image from snapshot
                            String num_of_replies = data.child("Replies").getChildrenCount() + "";//getting number of replies from snapshot
                            String username = data.child("username").getValue(String.class);//getting username from snapshot
                            Post post = new Post(id, username, b, URL, t);//creating new instance of a post
                            post.setNum_of_replies(num_of_replies);//establishing number of replies per post
                            try {
                                post.convertDate();//converting the data if there is one.
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Posts.add(post);//adding a post to the array of posts
                        } catch (Exception e) {
                        }

                    }
                    Posts.sort(new DateComparator());//comparing the posts by their data.
                    display_posts(Posts, false, false);//displaying all the posts.
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


        if (Posts.size() > 0) {//chekcing if their are posts availible

            lp.setOrientation(LinearLayout.VERTICAL);//setting layout orientation
            lp.removeAllViews();//removing all posts that where there previosulsy
            for (Post post : Posts) {//iterating therough all the posts
                try {
                    String uid = post.getID();//getting post id from snapshot
                    String post_body = post.getBody();//getting body from snapshot
                    String post_time = post.getTime().substring(0, 10);//getting time from snapshot
                    String URL = post.getPost_image_url();//getting image from snapshot
                    String ID = post.getID();//getting post ID from snapshot
                    String username_post = post.getUsername();//getting posts user from snapshot
                    String num_of_replies = post.getNum_of_replies();//getting number of replies for the post from snapshot


                    TextView usernameView;

                    boolean account_main = false;//checking for logged in user
                    if (!is_searched_user) {//checking is main user
                        if (username_post.equalsIgnoreCase(username)) {
                            usernameView = views.createUsernameTextView(Display_Hashtag_Posts.this, "Me");//setting text to show its main account that posted
                            account_main = true;//main account
                        } else {
                            usernameView = views.createUsernameTextView(Display_Hashtag_Posts.this, username_post);//setting text to show its searched account that posted
                        }
                    } else {
                        if (username_post.equalsIgnoreCase(account_user)) {
                            usernameView = views.createUsernameTextView(Display_Hashtag_Posts.this, "Me");//setting text to show its main account that posted
                            account_main = true;
                        } else {
                            usernameView = views.createUsernameTextView(Display_Hashtag_Posts.this, username_post);//setting text to show its searched account that pos
                        }
                    }

                    if (username_post.length() > 2) {
                        if (username_post.substring(0, 2).equalsIgnoreCase("Me")) {//setting text to show its main account that posted
                            account_main = true;
                        }
                    }

                    LinearLayout hl = views.createHorizontalLayout(Display_Hashtag_Posts.this);//setting orientation
                    hl.setGravity(Gravity.NO_GRAVITY);//text will fill the space
                    hl.addView(usernameView);//adding who posed the postto the layout

                    String new_post_body = post_body + " ";//stringable post
                    SpannableString spanString = processHashtag(new_post_body, uid, URL, post_time, username_post);//creating a spannable string
                    TextView body = views.createBodyTextViewHashtag(Display_Hashtag_Posts.this,spanString);//creating a spannable textview


                    TextView time = views.createTimeTextView(Display_Hashtag_Posts.this, post_time);//adding a time view

                    LinearLayout postview = views.createPostLayout(Display_Hashtag_Posts.this);////adding a post view
                    hl.addView(time);//adding time view
                    postview.addView(hl);//adding post view

                    if (URL.length() >= 1) {//if post body is a url
                        ImageView image = views.createImageView(Display_Hashtag_Posts.this,this,URL);//creating an image view for post
                        postview.addView(image);//adding image to post
                    }

                    ToggleButton favouritesButton = createFavouriteToggleButton(username, username_post, ID);
                    LinearLayout horizontalLayout = views.createHorizontalLayout(Display_Hashtag_Posts.this);
                    postview.addView(body);
                    if (!num_of_replies.equalsIgnoreCase("")) {//checking for number of replies
                        TextView replies = createNumOfReplies(num_of_replies);//adding a reply counter
                        horizontalLayout.addView(replies);//displaying number of replies
                    }

                    if (!account_main) {//id searched user
                        horizontalLayout.addView(favouritesButton);//adding a favourite option for each post
                        horizontalLayout.addView(createReplyOption(username_post, post_body, uid));//adding a repl option to each post
                    }

                    if (!Edits) {//checking if its not an edit
                        postview.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Display_Hashtag_Posts.this, Replies.class);
                                intent.putExtra("username", account_user);//adding value to intent
                                intent.putExtra("loggedinuser", account_user);//adding value to intent
                                intent.putExtra("ID", ID);//adding value to intent
                                intent.putExtra("post_body", post_body);//adding value to intent
                                intent.putExtra("URL", URL);//adding value to intent
                                intent.putExtra("post_time", post_time);//adding value to intent
                                intent.putExtra("username_post", username_post);//adding value to intent
                                intent.putExtra("is_searched_user", false);//adding value to intent
                                Display_Hashtag_Posts.this.startActivity(intent);//starting activity to display all the related hashtags
                                Display_Hashtag_Posts.this.finish();//ending intent
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


        dialogB.setView(popup_content);//displaying pop up screen
        dialog = dialogB.create();//creating a dialog to show pop up
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.popup_dialog_box);//setting background of drawble
        dialog.show();//shoeing popup


        popup_reply_button.setOnClickListener(new View.OnClickListener() {//function tot replyto a user post
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                String reply_msg = popup_reply_body.getText().toString();//getting perly to post

                Date date = new Date();//getting data of reply
                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");//format for data
                String t = (format.format(date));//formatting data

                DatabaseReference reply_ref = FirebaseDatabase.getInstance().getReference("Posts")//firebase ref for post table
                        .child(Reply_to_user).child(uid).child("Replies");
                reply_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long count = snapshot.getChildrenCount() + 1;//gettting number of replies
                        Post post = new Post(uid, account_user, reply_msg, "", t);//creating a new post instance
                        reply_ref.child(String.valueOf(count)).setValue(post);//adding new post to reply table
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                DatabaseReference add_reply_post = FirebaseDatabase.getInstance().getReference("Replies")//fireabse ref to replies table
                        .child(account_user);
                add_reply_post.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long count = snapshot.getChildrenCount() + 1;//increamenting number of replies to a post
                        Post post = new Post(String.valueOf(count), Reply_to_user, reply_msg, "", t);//creating ne wpost
                        add_reply_post.child(String.valueOf(count)).setValue(post);//adding reply to post
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                dialog.dismiss();//closing view
            }
        });
        ;
    }

    // This saves the liked post to the database
    public void addFavourite(String user, String userPost, String postID) {
        DatabaseReference favRef = FirebaseDatabase.getInstance().getReference("FavouritePosts").child(user).child(userPost);//fierebase ref
        favRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                favRef.child("ID").setValue(postID);//adding post to hashtag table
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }

    // This displays a hashtag in a post
    public SpannableString processHashtag(String body, String ID, String URL, String post_time, String username_post) {
        int index = body.indexOf("#"); // looks for the position of # in string
        int endIndex = body.indexOf(" ", index);//position of the end of the string
        String str = body.substring(index, endIndex);
        SpannableString spanString = new SpannableString(body);//creating a new spannalble clickable sting
        ForegroundColorSpan fcsCyan = new ForegroundColorSpan(Color.CYAN);//setting color

        spanString.setSpan(fcsCyan, index, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//creating clicakable string for hashtags

        return spanString;
    }


}