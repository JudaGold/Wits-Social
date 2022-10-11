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
    String ExistingBody, ExistingURL, ExistingTime, ExistingID, ExistingUsername;
    String username;
    String account_user;
    String hashtag;
    LinearLayout lp;
    DatabaseReference reference, reference2, reference3;// this the reference of the Firebase database
    long maxId = 1;

    String post_body, URL, post_time, ID, username_post;

    ArrayList<String> all_usernames;/* this will have the user's username
                                                           and the usernames of the users, the
                                                           user is following*/
    ArrayList<String> all_fcm_tokens;
    UI_Views views = new UI_Views();


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

        fetch_hashtag_posts(hashtag);

        lp = (LinearLayout) findViewById(R.id.post_layout);
        ImageButton btn_home = (ImageButton) findViewById(R.id.btnHome);

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void fetch_hashtag_posts(String hashtag) {
        ArrayList<Post> Posts = new ArrayList<Post>();

        reference = FirebaseDatabase.getInstance().getReference("Hashtags").child(hashtag.trim());
        Query hashtag_posts = reference.orderByChild(String.valueOf(maxId));
        hashtag_posts.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Log.i("Snapshot", "It exists");
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


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void fetchPosts(ArrayList<String> following) {
        ArrayList<Post> Posts = new ArrayList<Post>();

        for (String usernames : following) {
            reference = FirebaseDatabase.getInstance().getReference().child("Posts").child(usernames);
            Query following_posts = reference.orderByChild(String.valueOf(maxId));
            following_posts.addListenerForSingleValueEvent(new ValueEventListener() {
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
                                Post post = new Post(id, b, URL, t);
                                post.setNum_of_replies(num_of_replies);
                                post.setUsername(usernames);
                                try {
                                    post.convertDate();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                Posts.add(post);
                            } catch (Exception e) {
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        reference = FirebaseDatabase.getInstance().getReference().child("Replies").child(username);
        Query replies = reference.orderByChild(String.valueOf(maxId));
        replies.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    try {
                        String id = data.getKey();
                        String b = data.child("body").getValue(String.class);
                        String t = data.child("time").getValue(String.class);
                        String URL = data.child("post_image_url").getValue(String.class);
                        String username_post = data.child("username").getValue(String.class);
                        Post post = new Post(id, b, URL, t);
                        post.setUsername("Me replied to " + username_post);
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

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

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
                    LinearLayout horizontalLayout = createHorizontalLayout();
                    postview.addView(body);
                    if (!num_of_replies.equalsIgnoreCase("")) {
                        TextView replies = createNumOfReplies(num_of_replies);
                        horizontalLayout.addView(replies);
                    }

                    if (!account_main) {
                        horizontalLayout.addView(favouritesButton);
                        horizontalLayout.addView(createReplyOption(username_post, post_body, uid));
                    }

                    if (username_post.equalsIgnoreCase(username) && !Edits) {
                        postview.setOnLongClickListener(new View.OnLongClickListener() { //lets you long press to edit post
                            @Override
                            public boolean onLongClick(View view) {
                                setBody(post_body);
                                setURL(URL);
                                setID(ID);
                                setTime(post_time);
                                //showPopupMenu(postview); //shows popup edit option
                                return true;
                            }
                        });
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

    public void getFollowing() {
        all_usernames = new ArrayList<>();/* this will have the user's username
                                                           and the usernames of the users, the
                                                           user is following*/
        all_usernames.add(username);
        reference = FirebaseDatabase.getInstance().getReference("social")
                .child(username).child("following");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    String following_username = data.getValue(String.class);
                    all_usernames.add(following_username);
                }
                lp.removeAllViews();
                fetchPosts(all_usernames);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public ImageView createImageView() {
        ImageView imageView = new ImageView(Display_Hashtag_Posts.this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1100);
        params.gravity = Gravity.LEFT; //sets the image at the centre
        params.setMargins(0, 40, 0, 50);
        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        return imageView;
    }

    public void getImage(String URL, ImageView image) {
        Glide.with(Display_Hashtag_Posts.this).load(URL).into(image); /*gets image from the internet and adds
                                                                                            it to imageView*/
    }
    /*public TextView createBodyTextView(String str){
        TextView body = new TextView(Display_Hashtag_Posts.this);
        SpannableString sp = Create_Link(str);
        body.setText(sp);
        body.setTextSize(20);
        body.setTextColor(Color.parseColor("white"));
        body.setPadding(30,30,30,30);
        body.setMovementMethod(LinkMovementMethod.getInstance());
        return body;
    }*/

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

    public TextView createTimeTextView(String str) {
        TextView time = new TextView(Display_Hashtag_Posts.this);
        time.setText(str);
        time.setGravity(Gravity.RIGHT);
        time.setTextSize(11);
        time.setTextColor(Color.parseColor("white"));
        time.setPadding(0, 5, 20, 0);
        return time;
    }

    public LinearLayout createPostLayout() {
        LinearLayout post = new LinearLayout(Display_Hashtag_Posts.this);
        post.setOrientation(LinearLayout.VERTICAL);
        post.setBackground(ContextCompat.getDrawable(Display_Hashtag_Posts.this, R.drawable.post_layout));
        post.setPadding(30, 30, 20, 30);
        return post;
    }

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

//    public ImageView createRepliesIcon()
//    {
//        ImageView replies_icon = new ImageView(getContext());
//        replies_icon.setImageResource(R.drawable.ic_round_chat_bubble_outline_24);
//        replies_icon.setForegroundGravity(Gravity.RIGHT);
//        replies_icon.setPadding(30,0,5,0);
//
//        return replies_icon;
//    }

    public Space addSpace() {
        Space space = new Space(Display_Hashtag_Posts.this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 50);
        space.setLayoutParams(params);
        return space;
    }

    public LinearLayout createHorizontalLayout() {
        LinearLayout horizontalLayout = new LinearLayout(Display_Hashtag_Posts.this);
        horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
        horizontalLayout.setHorizontalGravity(Gravity.RIGHT);
        horizontalLayout.setPadding(30, 10, 20, 20);
        return horizontalLayout;
    }

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

   /* public void showPopupMenu(LinearLayout ll){
        PopupMenu popup_menu = new PopupMenu(Display_Hashtag_Posts.this, ll); //shows popup edit menu only on the post
        popup_menu.setOnMenuItemClickListener();
        popup_menu.inflate(R.menu.popup_edit);
        popup_menu.show();
    }*/

    /*adds the details of the post that must be edited to global variables so that it
    can be used by other classes
     */
    public void setBody(String b) {
        ExistingBody = b;
    }

    public void setURL(String url) {
        ExistingURL = url;
    }

    public void setID(String ID) {
        ExistingID = ID;
    }

    public void setTime(String time) {
        ExistingTime = time;
    }

    public void setUsername(String username) {
        ExistingUsername = username;
    }


    public void fetch_fcm_tokens() {
        all_fcm_tokens = new ArrayList<>();/* this will have the user's username
                                                           and the usernames of the users, the
                                                           user is following*/
        reference3 = FirebaseDatabase.getInstance().getReference("Notifications")
                .child(username);
        reference3.addListenerForSingleValueEvent(new ValueEventListener() {

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    String fcm_token = data.getValue(String.class);
                    all_fcm_tokens.add(fcm_token);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    /*  public boolean isPostChanged(String body, String imgURL){
          if (!body.equals(ExistingBody)||!imgURL.equals(ExistingURL)){
              return true;
          }
          else {
              return false;
          }
      }*/
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
                getFollowing();
            }
        });
        ;
    }

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

    public void initializeFavourites(String user, String userPost, String postID) {
        DatabaseReference favRef = FirebaseDatabase.getInstance().getReference("FavouritePosts").child(user).child(userPost);
    }


    /*public SpannableString Create_Link(String body){
        Analysis textAnalysyis = new Analysis(body);
        ArrayList<Pair<Integer,Integer>>data = new ArrayList<Pair<Integer,Integer>>();
        data = textAnalysyis.Find_link();
        SpannableString spannableString = new SpannableString(body);
        if(data.size() > 0) {
            for (Pair it : data) {

                int a = Integer.parseInt("" + it.first);
                int b = Integer.parseInt("" + it.second);
                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        String url = body.substring(a,b).toLowerCase(Locale.ROOT);
                        Uri uri = Uri.parse(url);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                };
                spannableString.setSpan(clickableSpan,a,b,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return spannableString;
    }*/

    public boolean checkHashtag(String body) {
        int index = body.indexOf("#"); // looks for the position of # in string
        if (index != -1) { //index of produces a -1 if it cannot find the substring
            return true;
        } else {
            return false;
        }
    }


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