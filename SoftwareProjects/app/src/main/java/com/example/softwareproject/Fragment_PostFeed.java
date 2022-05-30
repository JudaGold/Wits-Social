package com.example.softwareproject;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
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
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;


public class Fragment_PostFeed extends Fragment {

    ImageView image_popup, imgClose_popup;
    View v;
    EditText popup_post_body, popup_post_image;
    Button popup_add_post;
    ImageButton btnadd_post;
    DatabaseReference reference;// this the reference of the Firebase database
    long maxId = 1;
    String username,Luser;
    LinearLayout lp;
    ArrayList<String> all_usernames;/* this will have the user's username
                                                           and the usernames of the users, the
                                                           user is following*/
    Hashtable<String, Integer> username_colours;/* this will have the unique
                                                                           colour of each user*/
    Search_User_class su = new Search_User_class();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v =  inflater.inflate(R.layout.fragment__post_feed, container, false);
        Intent intent = getActivity().getIntent();
        username = intent.getStringExtra("username");
        Luser = intent.getStringExtra("loggedinuser");
        btnadd_post = (ImageButton) v.findViewById(R.id.btn_add_post);
        if(username.equalsIgnoreCase(Luser)){

            btnadd_post.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    add_post();
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
                    intent.putExtra("username", Luser);
                    intent.putExtra("loggedinuser",Luser);
                    getActivity().startActivity(intent);
                    getActivity().finish();
                }
            });
            display_searched_user_posts();

        }



        return v;
    }

    public void add_post() {
        AlertDialog.Builder dialogB = new AlertDialog.Builder(v.getContext());
        AlertDialog dialog;
        final View popup_content = getLayoutInflater().inflate(R.layout.popup_post, null);
        popup_post_body = (EditText) popup_content.findViewById(R.id.post_body);
        popup_post_image = (EditText) popup_content.findViewById(R.id.post_image);
        popup_add_post = (Button) popup_content.findViewById(R.id.btn_post);
        dialogB.setView(popup_content);
        dialog = dialogB.create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.popup_dialog_box);
        dialog.show();

        popup_add_post.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

                try {
                    String body = popup_post_body.getText().toString();
                    String image_url = popup_post_image.getText().toString();
                    Date date = new Date();
                    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    String t = (format.format(date));


                    reference = FirebaseDatabase.getInstance().getReference("Posts").child(username);
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                maxId = (snapshot.getChildrenCount()) + 1;
                            }
                            Post  post = new Post(""+maxId,body.trim(), image_url, t);
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
    public void fetchPosts(ArrayList<String> following, Hashtable<String, Integer> username_colours) {
        ArrayList<Post> Posts = new ArrayList<>();

        for (String usernames : following) {
            reference = FirebaseDatabase.getInstance().getReference().child("Posts").child(usernames);
            Query following_posts = reference.orderByChild(String.valueOf(maxId));
            following_posts.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        String id = data.getKey();
                        String b = data.child("body").getValue(String.class);
                        String t = data.child("time").getValue(String.class);
                        String URL = data.child("post_image_url").getValue(String.class);
                        Post post = new Post(id,b, URL, t);
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void display_posts(ArrayList<Post> Posts, Hashtable<String, Integer> username_colours) {
        lp.setOrientation(LinearLayout.VERTICAL);
        lp.setBackgroundColor(Color.parseColor("white"));
        lp.removeAllViews();
        for (Post post : Posts) {
            String post_body = post.getBody();
            String post_time = post.getTime();
            String URL = post.getPost_image_url();
            String username_post = post.getUsername();

            TextView usernameView = new TextView(v.getContext());
            usernameView.setTextSize(20);

            if (username_post.equalsIgnoreCase(username))
            {
                usernameView.setText("Me");
            }
            else
            {
                usernameView.setText(username_post);
            }
            usernameView.setTextColor(Color.parseColor("#135A71"));
            usernameView.setGravity(Gravity.CENTER);
            lp.addView(usernameView);

            TextView body = createBodyTextView(" "+post_body);
            TextView time = createTimeTextView(post_time);

            LinearLayout postview = createPostLayout();
            postview.addView(time);
            postview.addView(body);

            if (URL.length() >= 1) {
                ImageView image = createImageView();
                getImage(URL, image);
                postview.addView(image);

                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        create_img_popup(URL);
                    }
                });
            }

            lp.addView(postview);
        }
    }

    public void getFollowing() {
        all_usernames = new ArrayList<>();/* this will have the user's username
                                                           and the usernames of the users, the
                                                           user is following*/
         username_colours = new Hashtable<>();/* this will have the unique
                                                                       colour of each user*/
        all_usernames.add(username);
        lp = (LinearLayout) v.findViewById(R.id.scroll_posts);
        reference = FirebaseDatabase.getInstance()
                .getReference("social")
                .child(username)
                .child("following");
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
                lp.removeAllViews();
                fetchPosts(all_usernames, username_colours);
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
        Query posts =bd.orderByChild(String.valueOf(maxId));
        posts.addListenerForSingleValueEvent(new ValueEventListener() {
            @android.support.annotation.RequiresApi(api = Build.VERSION_CODES.O)
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
                    String URL = post_data.elementAt(i).getPost_image_url();

                    TextView body = createBodyTextView("\t"+post_body);
                    TextView time = createTimeTextView(post_time);
                    LinearLayout post = createPostLayout();

                    post.addView(time);
                    post.addView(body);

                    if (URL.length() >= 1) {
                        ImageView im = createImageView();
                        getImage(URL, im);
                        post.addView(im);
                        im.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                create_img_popup(URL);
                            }
                        });

                    }
                    Space space = addSpace();
                    lp.addView(post);
                    lp.addView(space); //adds space so that the posts look better
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void create_img_popup(String URL){
        AlertDialog.Builder dialogBuilder= new AlertDialog.Builder(v.getContext()); //used to show the popup screen
        AlertDialog dialog;
        final View imagePopupView = getLayoutInflater().inflate(R.layout.popup_image, null); //make the layout a popup
        image_popup = (ImageView) imagePopupView.findViewById(R.id.popup_image);
        imgClose_popup = (ImageView) imagePopupView.findViewById(R.id.imgClose);

        getImage(URL, image_popup); //gets the image from the internet and displays it in the imageView
                                    //view the class below
        dialogBuilder.setView(imagePopupView);
        dialog = dialogBuilder.create();
        dialog.show();

        imgClose_popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    public ImageView createImageView(){
        ImageView imageView = new ImageView(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(600, 600);
        params.gravity = Gravity.CENTER; //sets the image at the centre
        imageView.setLayoutParams(params);
        imageView.setClickable(true);
        return imageView;
    }

    public void getImage(String URL, ImageView image){
        Glide.with(Fragment_PostFeed.this).load(URL).into(image); /*gets image from the internet and adds
                                                                                            it to imageView*/
    }

    public TextView createBodyTextView(String str){
        TextView body = new TextView(getContext());
        body.setText(str);
        body.setTextSize(20);
        body.setPadding(30,30,30,30);
        return body;
    }

    public TextView createTimeTextView(String str){
        TextView time = new TextView(getContext());
        time.setText(str);
        time.setGravity(Gravity.RIGHT);
        time.setTextSize(15);
        return time;
    }

    public LinearLayout createPostLayout(){
        LinearLayout post = new LinearLayout(getContext());
        post.setOrientation(LinearLayout.VERTICAL);
        post.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.post_layout));
        post.setPadding(20,30,20,30);
        return post;
    }

    public Space addSpace(){
        Space space = new Space(v.getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,50);
        space.setLayoutParams(params);
        return space;
    }

}