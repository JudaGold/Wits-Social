package com.example.softwareproject;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.net.Uri;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
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
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

//import com.google.firebase.messaging.Message;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;
import java.util.*;


public class Fragment_PostFeed extends Fragment implements PopupMenu.OnMenuItemClickListener {
    private static final int PICK_IMAGE_REQUEST = 1;

    String ExistingBody, ExistingURL, ExistingTime, ExistingID, ExistingUsername;
    View v;
    Uri mImgUri;
    VideoView videoView;
    ImageView imgView;
    Boolean camera = true;
    EditText popup_post_body;
    private StorageReference mstorageRef;
    private DatabaseReference mDatabaseRef;

    Button popup_add_post;
    ImageButton popup_upload_media,pop_up_camera_btn, popup_img_btn, popup_vid_btn, popup_gif_btn;
    ImageButton btnadd_post;

    DatabaseReference reference, reference2, reference3, reference4;// this the reference of the Firebase database
    long maxId = 1;
    String username, account_user;
    LinearLayout lp, upload_layout;
    ArrayList<String> all_usernames;/* this will have the user's username
                                                           and the usernames of the users, the
                                                            user is following*/
    ArrayList<String>BlockedUsers;
    View popup_content,popup_content2;
    Search_User_class su = new Search_User_class();
    ArrayList<String> all_fcm_tokens;
    UI_Views views = new UI_Views();
    public final int CAMERA_REQUEST = 1888;
    public final int MY_CAMERA_PERMISSION_CODE = 100;
    String Image_from_camera = "1";
    private static final int pic_int = 123;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment__post_feed, container, false);
        Intent intent = getActivity().getIntent();
        username = intent.getStringExtra("username");
        account_user = intent.getStringExtra("loggedinuser");
        btnadd_post = (ImageButton) v.findViewById(R.id.btn_add_post);

        mstorageRef  = FirebaseStorage.getInstance().getReference("upload_gifs");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Posts");
        if (username.equalsIgnoreCase(account_user)) {

            fetch_fcm_tokens();
            btnadd_post.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onClick(View v) {
                    add_post(false, null, null, null, null);
                }
            });

            getFollowing();

        }
        else {
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
            blocked_user();//checks if user is block, prevent them from seeing posts
        }


        return v;
    }


    public void blocked_user(){//function to check is user is currently block by another user
        DatabaseReference b_ref = FirebaseDatabase.getInstance().getReference("social").child(username).child("Blocking");
        b_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean temp =false;
                if(snapshot.exists()){
                    for(DataSnapshot ds: snapshot.getChildren()){
                        if(ds.getValue(String.class).equalsIgnoreCase(account_user)){
                            temp = true;
                            break;
                        }
                    }

                }
                if(!temp){display_searched_user_posts();}//prevents blocked users from seeing posts
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void showUpload(){
        AlertDialog.Builder dialogB = new AlertDialog.Builder(v.getContext());
        AlertDialog dialog;
        final View popup_content2 = getLayoutInflater().inflate(R.layout.popup_upload, null);
        dialogB.setView(popup_content2);
        dialog = dialogB.create();
        dialog.show(); //creates s dialog to open a pop up window and communicate with it
        pop_up_camera_btn = (ImageButton) popup_content2.findViewById(R.id.CameraBtn);////instantiating  button to use the camera api
        pop_up_camera_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera = true;
                if(popup_content2.getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
                    Intent Camera_Intent  = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(Camera_Intent,pic_int);//overriden function to store picture from camera
                    dialog.dismiss();

                }else{
                    Toast.makeText(popup_content2.getContext(), "Camera not available",Toast.LENGTH_SHORT).show();
                }

            }
        });

        popup_vid_btn = (ImageButton) popup_content2.findViewById(R.id.vidBtn);
        popup_vid_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*videoView = views.createVideoView(popup_content2.getContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1100);
                params.gravity = Gravity.CENTER_HORIZONTAL; //sets the image at the centre
                params.setMargins(0, 40, 0, 50);
                videoView.setLayoutParams(params);*/
            }
        });

        popup_gif_btn =(ImageButton) popup_content2.findViewById(R.id.gifBtn);
        popup_gif_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camera = false;
                upload_layout = (LinearLayout) popup_content2.findViewById(R.id.upload_layout);
                imgView = views.previewImageView(popup_content2.getContext());

                Button button = views.createButton(popup_content2.getContext(), "gif");
                openFileUser("image/gif");
                upload_layout.addView(imgView);
                upload_layout.setBackgroundResource(R.drawable.button_shape_square);
                upload_layout.addView(button);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //uploadFile();
                    }
                });
            }
        });

        popup_img_btn = (ImageButton) popup_content2.findViewById(R.id.imgBtn);
        popup_img_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camera = false;
                upload_layout = (LinearLayout) popup_content2.findViewById(R.id.upload_layout);
                imgView = views.previewImageView(popup_content2.getContext());

                Button button = views.createButton(popup_content2.getContext(), "image");
                openFileUser("image/*");
                upload_layout.addView(imgView);
                upload_layout.setBackgroundResource(R.drawable.button_shape_square);
                upload_layout.addView(button);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //uploadFile();
                        dialog.dismiss();
                    }
                });
            }
        });


    }

    /*public void uploadFile(){
        if(mImgUri != null){
            StorageReference fileReference = mstorageRef.child(System.currentTimeMillis() + "." + getFileExtension(mImgUri));
            fileReference.putFile(mImgUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDatabaseRef.child(username).child("").child("post_image_url").setValue(mImgUri.toString());
                            Toast.makeText(popup_content2.getContext(), "upload successfull",Toast.LENGTH_SHORT);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(popup_content2.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }*/

    private void openFileUser(String type){
        Intent intent = new Intent();
        intent.setType(type);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri){//fucntion to get the compression type from the picture the user tool
        ContentResolver cR = getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));//return string to upload.
    }

    public Uri convert_bitmap(Context context,Bitmap bitmap){
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    // This method will load the chosen image onto the image view
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (camera){
            if (requestCode == pic_int) { //checking if app has permission to use camera
            Bitmap photo = (Bitmap) data.getExtras().get("data");//storing the image in a bitmap
            ByteArrayOutputStream baos = new ByteArrayOutputStream();//instantiating a bitmap stream to use to convert to a string button
            photo.compress(Bitmap.CompressFormat.PNG, 100, baos);//rearranging the bitmap into a picture form

            Uri temp  =convert_bitmap(getContext(),photo);//converting bitmap to uri to store in firebase
            StorageReference dbref = FirebaseStorage.getInstance().getReference("Post_pictures")//ref to store image
                    .child(System.currentTimeMillis() + "." + getFileExtension(temp));;
            StorageTask mUploadTask =dbref.putFile(temp).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    dbref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Image_from_camera = temp.toString();
                        }//storing local var as the images string version.
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("ERROR","did not work");
                }
            });
            }
        }
        else {
            if ((requestCode == PICK_IMAGE_REQUEST)
                    && (data != null) && (data.getData() != null)) {
                mImgUri = data.getData();
                //Picasso.get().load(mVideUri).into(videoView);
                Glide.with(this).load(mImgUri).into(imgView);
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void add_post(Boolean edit, String body, String URL, String Id, String time) {

        AlertDialog.Builder dialogB = new AlertDialog.Builder(v.getContext());
        AlertDialog dialog;
        popup_content = getLayoutInflater().inflate(R.layout.popup_post, null);
        popup_post_body = (EditText) popup_content.findViewById(R.id.post_body);
        popup_add_post = (Button) popup_content.findViewById(R.id.btn_post);
        popup_upload_media = (ImageButton) popup_content.findViewById(R.id.btn_upload);
        popup_upload_media.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUpload();
            }

        });

        if (edit) {
            if (URL.equalsIgnoreCase("")) {
                popup_post_body.setText(body);
            } else {
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
            } catch (Exception e) {
            }
        }
        dialogB.setView(popup_content);
        dialog = dialogB.create();
        dialog.show();
        popup_add_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    String body , image_url;
                    if(Image_from_camera.length()>1){//checking if user has taken a picture from the camera.
                        body = popup_post_body.getText().toString();
                        image_url = Image_from_camera;
                    }else if(popup_post_body.getText().toString().length() <=4 || popup_post_body.getText().toString().isEmpty()){
                        body = popup_post_body.getText().toString();
                        image_url = "";
                    }
                    else{
                        String post = popup_post_body.getText().toString();
                        if (post.substring(0, 4).equalsIgnoreCase("http")) {
                            body = post;
                            image_url = post;
                        } else {
                            body = post;
                            image_url = "";
                        }
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
                            } else {
                                if (snapshot.exists()) {
                                    maxId = (snapshot.getChildrenCount()) + 1;
                                }
                                post = new Post("" + maxId, body.trim(), image_url, t);
                                reference.child(String.valueOf(maxId)).setValue(post);
                            }
                            dialog.dismiss();
                            fetchPosts(all_usernames);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    for (String fcm_token : all_fcm_tokens) {
                        FcmNotificationsSender notificationsSender = new FcmNotificationsSender(fcm_token, username, body, getContext());
                        notificationsSender.SendNotifications();
                    }
                } catch (Exception e) {
                }

                String body = popup_post_body.getText().toString();
                boolean hashtag = checkHashtag(body);

                if (hashtag) {
                    int index = body.indexOf("#"); // looks for the position of # in string
                    int endIndex = body.length();
                    String tag = body.substring(index + 1, endIndex);

                    try {
                        String image_url;
                        if (body.substring(0, 4).equalsIgnoreCase("http")) {
                            image_url = body;
                        } else {
                            image_url = "";
                        }
                        Date date = new Date();
                        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                        String t = (format.format(date));
                        reference4 = FirebaseDatabase.getInstance().getReference("Hashtags").child(tag.trim());
                        reference4.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                long max_id = 1;
                                Post post;
                                if (snapshot.exists()) {
                                    max_id = (snapshot.getChildrenCount()) + 1;
                                }
                                post = new Post("" + max_id, username, body.trim(), image_url, t);
                                reference4.child(String.valueOf(max_id)).setValue(post);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    } catch (Exception e) {

                    }
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
        btnadd_post = (ImageButton) v.findViewById(R.id.btn_add_post);

        if (Posts.size() > 0) {
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
                try {
                    String uid = post.getID();
                    String post_body = post.getBody();
                    String post_time = post.getTime().substring(0, 10);
                    String URL = post.getPost_image_url();
                    String ID = post.getID();
                    String username_post = post.getUsername();
                    String num_of_replies = post.getNum_of_replies();

                    LinearLayout hl = views.createHorizontalLayout(getContext()); //creating horizontal linear
                    // layout for username and time
                    hl.setHorizontalGravity(Gravity.TOP); //reseting the gravity that was defined
                    TextView usernameView;
                    TextView body = null;

                    boolean account_main = false;//checking for logged in user
                    if (!is_searched_user) {
                        if (username_post.equalsIgnoreCase(username)) {
                            usernameView = views.createUsernameTextView(getContext(), "me");
                            account_main = true;
                        } else {
                            usernameView = views.createUsernameTextView(getContext(), username_post);
                        }
                    } else {//whats the point of this, till line 340
                        if (username_post.equalsIgnoreCase(account_user)) {
                            usernameView = views.createUsernameTextView(getContext(), "me");
                            account_main = true;
                        } else {
                            usernameView = views.createUsernameTextView(getContext(), username_post);
                        }
                    }

                    if (username_post.length() > 2) {
                        if (username_post.substring(0, 2).equalsIgnoreCase("Me")) {
                            account_main = true;
                        }
                    }

                    boolean hashtag = checkHashtag(post_body);
                    if (hashtag) {
                        String new_post_body = post_body + " ";
                        SpannableString spanString = processHashtag(new_post_body, uid, URL, post_time, username_post);
                        body = createBodyTextViewHashtag(spanString);
                    } else {
                        body = views.createBodyTextView(getContext(), getActivity(), " " + post_body);
                    }

                    TextView time = views.createTimeTextView(getContext(), post_time);

                    LinearLayout postview = views.createPostLayout(getContext());

                    //adding username and time textview to the horizontal layout
                    hl.addView(usernameView);
                    hl.addView(time);
                    postview.addView(hl);

                    if (URL.length() >= 1) {
                        postview.addView(views.createImageView(getContext(), getActivity(), URL));
                    }


                    postview.addView(body);
                    ToggleButton favouritesButton = createFavouriteToggleButton(username, username_post, ID);
                    LinearLayout horizontalLayout = views.createHorizontalLayout(getContext());

                    if (!account_main) {
                        horizontalLayout.addView(favouritesButton);
                        horizontalLayout.addView(createReplyOption(username_post, post_body, uid));
                    }
                    if (!num_of_replies.equalsIgnoreCase("")) {
                        TextView replies = createNumOfReplies(num_of_replies);
                        horizontalLayout.addView(replies);
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
                    lp.addView(views.addSpace(getContext()));
                    lp.addView(postview);


                } catch (Exception e) {

                }
            }
        }
    }

    public void getFollowing() {
        su = new Search_User_class();
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

    public void display_searched_user_posts() {
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
                for (DataSnapshot data : snapshot.getChildren()) {
                    String id = data.getKey();
                    String b = data.child("body").getValue(String.class);
                    String t = data.child("time").getValue(String.class);
                    String URL = data.child("post_image_url").getValue(String.class);
                    post_data.add(new Post(id, b, URL, t));
                }
                for (int i = post_data.size() - 1; i >= 0; i--) {
                    String post_body = post_data.elementAt(i).getBody();
                    String post_time = post_data.elementAt(i).getTime();
                    String uid = post_data.elementAt(i).getID();
                    post_time = post_time.substring(0, 10);
                    String URL = post_data.elementAt(i).getPost_image_url();

                    TextView body = null;
                    boolean hashtag = checkHashtag(post_body);
                    if (hashtag) {
                        String new_post_body = post_body + " ";
                        SpannableString spanString = processHashtag(new_post_body, uid, URL, post_time, username);
                        body = createBodyTextViewHashtag(spanString);
                    } else {
                        body = views.createBodyTextView(getContext(), getActivity(), "\t" + post_body);
                    }
                    TextView time = views.createTimeTextView(getContext(), post_time);
                    LinearLayout post = views.createPostLayout(getContext());

                    post.addView(time);

                    if (URL.length() >= 1) {
                        post.addView(views.createImageView(getContext(), getActivity(), URL));
                    }

                    post.addView(body);
                    Space space = views.addSpace(getContext());
                    ToggleButton favouritesButton = createFavouriteToggleButton(account_user, username, uid);
                    LinearLayout horizontalLayout = views.createHorizontalLayout(getContext());
                    horizontalLayout.addView(favouritesButton);
                    horizontalLayout.addView(createReplyOption(username, post_body, uid));

                    post.addView(horizontalLayout);

                    lp.addView(space); //adds space so that the posts look better
                    lp.addView(post);


                    String finalPost_time = post_time;
                    post.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(), Replies.class);
                            intent.putExtra("username", account_user);
                            intent.putExtra("loggedinuser", account_user);
                            intent.putExtra("ID", uid);
                            intent.putExtra("post_body", post_body);
                            intent.putExtra("URL", URL);
                            intent.putExtra("post_time", finalPost_time);
                            intent.putExtra("username_post", username);
                            intent.putExtra("is_searched_user", false);
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

    public TextView createBodyTextViewHashtag(SpannableString str) {
        TextView body = new TextView(getContext());
        body.setText(str);
        body.setTextSize(20);
        body.setMovementMethod(LinkMovementMethod.getInstance());
        body.setTextColor(Color.parseColor("white"));
        body.setPadding(30, 30, 30, 30);
        body.setMovementMethod(LinkMovementMethod.getInstance());
        return body;
    }

    public TextView createReplyOption(String Reply_to, String post_msg, String uid) {//adding a reply text for user to click on to reply to a post
        TextView textView = new TextView(getContext());
        textView.setText("reply");
        textView.setTextSize(18);
        textView.setGravity(Gravity.RIGHT);
        textView.setPadding(20, 0, 20, 0);
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


    public TextView createNumOfReplies(String num_of_replies) {
        TextView textView = new TextView(getContext());
        textView.setText(num_of_replies);
        textView.setTextSize(10);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(80, 80);
        textView.setLayoutParams(params);
        params.bottomMargin = 4;
        textView.setPadding(0, 0, 0, 15);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.parseColor("white"));
        textView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_round_chat_bubble_outline_24));

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


    public ToggleButton createFavouriteToggleButton(String user, String userPost, String ID) {
        ToggleButton toggleButton = new ToggleButton(getContext());
        toggleButton.setText(""); //removes all text from the toggle button so that only the heart shows
        toggleButton.setTextOn("");
        toggleButton.setTextOff("");
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(80, 80);
        toggleButton.setLayoutParams(params);
        toggleButton.setPadding(30, 0, 190, 0);
        toggleButton.setBackgroundResource(R.drawable.toggle_selector);
        toggleButton.setClickable(true);

        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (toggleButton.isChecked()) {
                    Toast.makeText(getContext(), "added to favourites", Toast.LENGTH_SHORT).show();
                    addFavourite(user, userPost, ID);
                } else {
                    Toast.makeText(getContext(), "removed from favourites", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return toggleButton;
    }

    public void showPopupMenu(LinearLayout ll) {
        PopupMenu popup_menu = new PopupMenu(v.getContext(), ll); //shows popup edit menu only on the post
        popup_menu.setOnMenuItemClickListener(this);
        popup_menu.inflate(R.menu.popup_edit);
        popup_menu.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.edit_post:
                add_post(true, ExistingBody, ExistingURL, ExistingID, ExistingTime);
                return true;
            case R.id.view_edited_posts:
                ArrayList<Post> Posts = new ArrayList<>();
                Post post = new Post(ExistingID, ExistingBody, ExistingURL, ExistingTime);
                post.setUsername(username);
                try {
                    post.convertDate();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Posts.add(post);
                getPreviousEdits(ExistingID, Posts);
                return true;
            default:
                return false;
        }
    }

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

    public void getPreviousEdits(String Id, ArrayList<Post> Posts) {

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
        AlertDialog.Builder dialogB = new AlertDialog.Builder(v.getContext());
        AlertDialog dialog;
        final View popup_content = getLayoutInflater().inflate(R.layout.pop_up_reply, null);
        TextView popup_header = (TextView) popup_content.findViewById(R.id.reply_header);
        TextView popup_original = (TextView) popup_content.findViewById(R.id.post_replying_to);
        //popup_original.setTextSize(11);
        EditText popup_reply_body = (EditText) popup_content.findViewById(R.id.reply_body);
        Button popup_reply_button = (Button) popup_content.findViewById(R.id.btn_reply);
        popup_header.setText("Replying to:\n\t" + Reply_to_user);
        popup_original.setText(original_post_msg);


        dialogB.setView(popup_content);
        dialog = dialogB.create();
        //dialog.getWindow().setBackgroundDrawableResource(R.drawable.popup_dialog_box);
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
                long postmaxid = snapshot.child("ID").getChildrenCount();
                if (!snapshot.child("ID").child(String.valueOf(postmaxid + 1)).exists()) {
                    favRef.child("ID").child(String.valueOf(postmaxid + 1)).setValue(postID);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }

    public void initializeFavourites(String user, String userPost, String postID) {
        DatabaseReference favRef = FirebaseDatabase.getInstance().getReference("FavouritePosts").child(user).child(userPost);
    }

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
        int endIndex = body.length();
        String str = body.substring(index + 1, endIndex);
        SpannableString spanString = new SpannableString(body);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(getActivity(), Display_Hashtag_Posts.class);
                intent.putExtra("username", account_user);
                intent.putExtra("loggedinuser", account_user);
                intent.putExtra("hashtag", str);
                intent.putExtra("ID", ID);
                intent.putExtra("post_body", body);
                intent.putExtra("URL", URL);
                intent.putExtra("post_time", post_time);
                intent.putExtra("username_post", username_post);
                intent.putExtra("is_searched_user", false);
                getActivity().startActivity(intent);
                getActivity().finish();
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.CYAN);
                ds.setUnderlineText(false);
            }
        };

        spanString.setSpan(clickableSpan, index, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spanString;
    }
}