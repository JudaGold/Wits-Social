package com.example.softwareproject;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
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
//import com.google.firebase.messaging.Message;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;
import java.util.*;


public class Fragment_PostFeed extends Fragment implements PopupMenu.OnMenuItemClickListener {

    String ExistingBody, ExistingURL, ExistingTime, ExistingID, ExistingUsername;

    ImageView image_popup, imgClose_popup;
    View v;
    EditText popup_post_body;
    Button popup_add_post;
    ImageButton btnadd_post;
    DatabaseReference reference,reference2, reference3;// this the reference of the Firebase database
    long maxId = 1;
    String username,account_user;
    LinearLayout lp;
    ArrayList<String> all_usernames;/* this will have the user's username
                                                           and the usernames of the users, the
                                                           user is following*/
    Search_User_class su = new Search_User_class();
    ArrayList<String> all_fcm_tokens;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v =  inflater.inflate(R.layout.fragment__post_feed, container, false);
        Intent intent = getActivity().getIntent();
        username = intent.getStringExtra("username");
        account_user = intent.getStringExtra("loggedinuser");
        btnadd_post = (ImageButton) v.findViewById(R.id.btn_add_post);
        if(username.equalsIgnoreCase(account_user)){

            fetch_fcm_tokens();
            btnadd_post.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onClick(View v) {
                    add_post(false,null,null,null, null);
                }
            });
            getFollowing();
        }
        else{
            btnadd_post.setImageResource(R.drawable.ic_baseline_home_24);
            btnadd_post.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), user_display.class);
                    intent.putExtra("username", account_user);
                    intent.putExtra("loggedinuser",account_user);
                    getActivity().startActivity(intent);
                    getActivity().finish();
                }
            });
            display_searched_user_posts();

        }



        return v;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void add_post(Boolean edit, String body, String URL, String Id, String time) {
        AlertDialog.Builder dialogB = new AlertDialog.Builder(v.getContext());
        AlertDialog dialog;
        final View popup_content = getLayoutInflater().inflate(R.layout.popup_post, null);
        popup_post_body = (EditText) popup_content.findViewById(R.id.post_body);
        popup_add_post = (Button) popup_content.findViewById(R.id.btn_post);

        if (edit){
            if (URL.equalsIgnoreCase(""))
            {
                popup_post_body.setText(body);
            }
            else
            {
                popup_post_body.setText(URL);
            }

            popup_add_post.setText("Edit Post");

            try {
                reference2 = FirebaseDatabase.getInstance().getReference("Posts").child(username).child(Id).child("Edits");
                reference2.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Post editing = null;
                        maxId = (snapshot.getChildrenCount()) + 1;
                        editing = new Post("" + maxId, body.trim(), URL, time);
                        reference2.child(String.valueOf(maxId)).setValue(editing);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }catch (Exception e) {
            }
        }

        dialogB.setView(popup_content);
        dialog = dialogB.create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.popup_dialog_box);
        dialog.show();

        popup_add_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    String body, image_url;
                    String post = popup_post_body.getText().toString();
                    if (post.substring(0, 4).equalsIgnoreCase("http"))
                    {
                        body = post;
                        image_url = post;
                    }
                    else {
                        body = post;
                        image_url = "";
                    }
                    Date date = new Date();
                    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    String t = (format.format(date));
                    reference = FirebaseDatabase.getInstance().getReference("Posts").child(username);
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Post post = null;
                            if (edit) {
                                Map<String, Object> dictionary = new HashMap<String, Object>();
                                dictionary.put("body", body.trim());
                                dictionary.put("time", t);
                                dictionary.put("post_image_url", image_url);
                                //post = new Post(""+Id,body.trim(), image_url, t);
                                reference.child(Id).updateChildren(dictionary);
                            }
                            else {
                                if (snapshot.exists()) {
                                    maxId = (snapshot.getChildrenCount()) + 1;
                                }
                                post = new Post(""+maxId,body.trim(), image_url, t);
                                reference.child(String.valueOf(maxId)).setValue(post);
                            }
                            dialog.dismiss();
                            fetchPosts(all_usernames);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    for (String fcm_token : all_fcm_tokens)
                    {
                        FcmNotificationsSender notificationsSender = new FcmNotificationsSender(fcm_token, username, body, getContext());
                        notificationsSender.SendNotifications();
                    }
                } catch (Exception e) {
                }
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
                    try{
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
                    }
                    catch(Exception e){
                    }}
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
        btnadd_post = (ImageButton) v.findViewById(R.id.btn_add_post);
        int counter_reply = 0;

        if (Posts.size() >0) {
        if (Edits) {
            btnadd_post.setImageResource(R.drawable.ic_baseline_home_24);
            btnadd_post.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), user_display.class);
                    intent.putExtra("username", account_user);
                    intent.putExtra("loggedinuser", account_user);
                    getActivity().startActivity(intent);
                    getActivity().finish();
                }
            });
        }

        lp.setOrientation(LinearLayout.VERTICAL);
        lp.removeAllViews();
        for (Post post : Posts) {
           try{
               String uid = post.getID();
               String post_body = post.getBody();
               String post_time = post.getTime().substring(0, 10);
               String URL = post.getPost_image_url();
               String ID = post.getID();
               String username_post = post.getUsername();
               String num_of_replies = post.getNum_of_replies();

               TextView usernameView = createUsernameTextView();
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

               if (username_post.length() > 2)
               {
                   if (username_post.substring(0, 2).equalsIgnoreCase("Me"))
                   {
                       account_main = true;
                   }
               }

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

               ToggleButton favouritesButton = createFavouriteToggleButton(username, username_post, ID);
               LinearLayout horizontalLayout = createHorizontalLayout();
               postview.addView(body);
               TextView replies = createNumOfReplies(num_of_replies);
               horizontalLayout.addView(replies);
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
                           showPopupMenu(postview); //shows popup edit option
                           return true;
                       }
                   });
               }

               if (!Edits) {
                   postview.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View view) {
                           Intent intent = new Intent(getActivity(), Replies.class);
                           intent.putExtra("username", account_user);
                           intent.putExtra("loggedinuser", account_user);
                           intent.putExtra("ID", ID);
                           intent.putExtra("post_body", post_body);
                           intent.putExtra("URL", URL);
                           intent.putExtra("post_time", post_time);
                           intent.putExtra("username_post", username_post);
                           intent.putExtra("is_searched_user", false);
                           getActivity().startActivity(intent);
                           getActivity().finish();
                       }
                   });
               }

               postview.addView(horizontalLayout);
               lp.addView(postview);
               lp.addView(addSpace());

           }catch(Exception e){

           }
        }
    }
    }
    public void getFollowing() {
        all_usernames = new ArrayList<>();/* this will have the user's username
                                                           and the usernames of the users, the
                                                           user is following*/
        all_usernames.add(username);
        lp = (LinearLayout) v.findViewById(R.id.scroll_posts);
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
    public void display_searched_user_posts(){
        LinearLayout lp = (LinearLayout) v.findViewById(R.id.scroll_posts);
        lp.setOrientation(LinearLayout.VERTICAL);
        lp.removeAllViews();
        DatabaseReference bd = FirebaseDatabase.getInstance().getReference().child("Posts").child(username);
        Query posts = bd.orderByChild(String.valueOf(maxId));
        posts.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Vector<Post> post_data;
                post_data = new Vector<>();
                for(DataSnapshot data:snapshot.getChildren()){
                    String id = data.getKey();
                    String b = data.child("body").getValue(String.class);
                    String t = data.child("time").getValue(String.class);
                    String URL = data.child("post_image_url").getValue(String.class);
                    post_data.add(new Post(id,b,URL,t));
                }
                for(int i = post_data.size()-1;i>=0;i--){
                    String post_body = post_data.elementAt(i).getBody();
                    String post_time = post_data.elementAt(i).getTime();
                    String uid = post_data.elementAt(i).getID();
                    post_time = post_time.substring(0,10);
                    String URL = post_data.elementAt(i).getPost_image_url();


                    TextView body = createBodyTextView("\t"+post_body);
                    TextView time = createTimeTextView(post_time);
                    LinearLayout post = createPostLayout();

                    post.addView(time);


                    if (URL.length() >= 1) {
                        ImageView im = createImageView();
                        getImage(URL, im);
                        post.addView(im);
                    }
                    body.setTextColor(Color.parseColor("#000000"));
                    post.addView(body);
                    Space space = addSpace();
                    ToggleButton favouritesButton = createFavouriteToggleButton(account_user,username,uid);
                    LinearLayout horizontalLayout = createHorizontalLayout();
                    horizontalLayout.addView(favouritesButton);
                    horizontalLayout.addView(createReplyOption(username,post_body,uid));

                    post.addView(horizontalLayout);

                    lp.addView(post);
                    lp.addView(space); //adds space so that the posts look better

                    String finalPost_time = post_time;
                    post.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(), Replies.class);
                            intent.putExtra("username", account_user);
                            intent.putExtra("loggedinuser",account_user);
                            intent.putExtra("ID",uid);
                            intent.putExtra("post_body",post_body);
                            intent.putExtra("URL",URL);
                            intent.putExtra("post_time",finalPost_time);
                            intent.putExtra("username_post",username);
                            intent.putExtra("is_searched_user",false);
                            getActivity().startActivity(intent);
                            getActivity().finish();
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public TextView createUsernameTextView(){
        TextView user = new TextView(getContext());
        user.setTextSize(20);
        user.setPadding(30,30,30,30);
        user.setTextColor(Color.parseColor("#FF47FAF3"));
        user.setGravity(Gravity.LEFT);
        return user;

    }
    public ImageView createImageView(){
        ImageView imageView = new ImageView(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1100);
        params.gravity = Gravity.LEFT; //sets the image at the centre
        params.setMargins(0,40,0,50);
        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        return imageView;
    }
    public void getImage(String URL, ImageView image){
        Glide.with(Fragment_PostFeed.this).load(URL).into(image); /*gets image from the internet and adds
                                                                                            it to imageView*/
    }
    public TextView createBodyTextView(String str){
        TextView body = new TextView(getContext());
        SpannableString sp = Create_Link(str);
        body.setText(sp);
        body.setTextSize(20);
        body.setTextColor(Color.parseColor("white"));
        body.setPadding(30,30,30,30);
        body.setMovementMethod(LinkMovementMethod.getInstance());
        return body;
    }

    public TextView createReplyOption(String Reply_to,String post_msg,String uid){//adding a reply text for user to click on to reply to a post
        TextView textView = new TextView(getContext());
        textView.setText("reply");
        textView.setTextSize(18);
        textView.setGravity(Gravity.RIGHT);
        textView.setPadding(30,0,20,0);
        textView.setTextColor(Color.parseColor("white"));

        textView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                Reply(Reply_to,post_msg,uid);
            }
        });
        return textView;
    }
    public TextView createTimeTextView(String str){
        TextView time = new TextView(getContext());
        time.setText(str);
        time.setGravity(Gravity.RIGHT);
        time.setTextSize(11);
        time.setTextColor(Color.parseColor("white"));
        time.setPadding(0,5,20,0);
        return time;
    }
    public LinearLayout createPostLayout(){
        LinearLayout post = new LinearLayout(getContext());
        post.setOrientation(LinearLayout.VERTICAL);
        post.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.post_layout));
        post.setPadding(30,30,20,30);
        return post;
    }

    public TextView createNumOfReplies(String num_of_replies)
    {
        TextView textView = new TextView(getContext());
        textView.setText(num_of_replies);
        textView.setTextSize(18);
        textView.setGravity(Gravity.RIGHT);
        textView.setPadding(30,0,20,0);
        textView.setTextColor(Color.parseColor("white"));

        return textView;
    }

    public Space addSpace(){
        Space space = new Space(v.getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,50);
        space.setLayoutParams(params);
        return space;
    }
    public LinearLayout createHorizontalLayout(){
        LinearLayout horizontalLayout = new LinearLayout(getContext());
        horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
        horizontalLayout.setHorizontalGravity(Gravity.RIGHT);
        horizontalLayout.setPadding(30,10,20,20);
        return horizontalLayout;
    }
    public ToggleButton createFavouriteToggleButton(String user, String userPost, String ID){
        ToggleButton toggleButton = new ToggleButton(getContext());
        toggleButton.setText(""); //removes all text from the toggle button so that only the heart shows
        toggleButton.setTextOn("");
        toggleButton.setTextOff("");
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(80,80);
        toggleButton.setLayoutParams(params);
        toggleButton.setPadding(30,0,200,0);
        toggleButton.setBackgroundResource(R.drawable.toggle_selector);
        toggleButton.setClickable(true);

        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (toggleButton.isChecked()){
                    Toast.makeText(getContext(), "added to favourites", Toast.LENGTH_SHORT).show();
                    addFavourite(user,userPost,ID);
                }
                else{
                    Toast.makeText(getContext(), "removed from favourites", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return toggleButton;
    }

    public void showPopupMenu(LinearLayout ll){
        PopupMenu popup_menu = new PopupMenu(v.getContext(), ll); //shows popup edit menu only on the post
        popup_menu.setOnMenuItemClickListener(this);
        popup_menu.inflate(R.menu.popup_edit);
        popup_menu.show();
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.edit_post:
                add_post(true,ExistingBody,ExistingURL, ExistingID, ExistingTime);
                return true;
            case R.id.view_edited_posts:
                ArrayList<Post> Posts = new ArrayList<>();
                Post post = new Post(ExistingID,ExistingBody, ExistingURL, ExistingTime);
                post.setUsername(username);
                try {
                    post.convertDate();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Posts.add(post);
                getPreviousEdits(ExistingID,Posts);
                return true;
            default:
                return false;
        }
    }
    /*adds the details of the post that must be edited to global variables so that it
    can be used by other classes
     */
    public void setBody(String b){
        ExistingBody = b;
    }
    public void setURL(String url){
        ExistingURL = url;
    }
    public void setID(String ID){
        ExistingID = ID;
    }
    public void setTime(String time){
        ExistingTime = time;
    }

    public void setUsername(String username)
    {
        ExistingUsername = username;
    }

    public void getPreviousEdits(String Id,ArrayList<Post> Posts) {

        lp = (LinearLayout) v.findViewById(R.id.scroll_posts);
        reference = FirebaseDatabase.getInstance().getReference("Posts").child(username).child(Id).child("Edits");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot data : snapshot.getChildren()) {
                        String id = data.getKey();
                        String b = data.child("body").getValue(String.class);
                        String t = data.child("time").getValue(String.class);
                        String URL = data.child("post_image_url").getValue(String.class);
                        Post post = new Post(id, b, URL, t);
                        post.setUsername(username);
                        try {
                            post.convertDate();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Posts.add(post);
                    }
                    Posts.sort(new DateComparator());
                    display_posts(Posts, true, false);
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void fetch_fcm_tokens()
    {
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
public void Reply(String Reply_to_user, String original_post_msg, String uid){
    AlertDialog.Builder dialogB = new AlertDialog.Builder(v.getContext());
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
                    Post post = new Post(uid,account_user,reply_msg,"",t);
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
            getFollowing();
        }
    });
  ;
}

public void addFavourite(String user, String userPost, String postID){
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

    public void initializeFavourites(String user, String userPost, String postID){
        DatabaseReference favRef = FirebaseDatabase.getInstance().getReference("FavouritePosts").child(user).child(userPost);}


    public SpannableString Create_Link(String body){
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
    }
}