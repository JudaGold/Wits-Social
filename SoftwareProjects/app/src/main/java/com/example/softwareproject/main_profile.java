package com.example.softwareproject;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.SortedMap;
import java.util.Vector;

public class main_profile extends AppCompatActivity {
    TextView t;
    EditText popup_bio_text,popup_post_body,popup_post_image;
    Button popup_Save_bio,popup_add_post;
    ImageButton btnadd_post;
    DatabaseReference reference;
    ImageView user_image;
    long maxId  = 1 ;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_profile);
        Intent intent = getIntent();
        t = (TextView) findViewById(R.id.user_bio);
        username = intent.getStringExtra("username");
        user_image = (ImageView) findViewById(R.id.user_image);
        set_username();
        display_posts();
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
                display_posts();
            }
        });


    }

    public void set_username( ) {
        reference = FirebaseDatabase.getInstance().getReference("Users");
        Query checkUser = reference.orderByChild("username").equalTo(username);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String bio = snapshot.child(username).child("bio").getValue(String.class);

                    if(bio.length()>=1){
                        t.setText(username+"\n"+bio);
                    }
                    else{
                        t.setText(username+"\nClick here to add something about yourself.");
                        t.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                add_bio();

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void add_bio( ){
        AlertDialog.Builder dialogBuilder= new AlertDialog.Builder(this);
        AlertDialog dialog;
        final View contented = getLayoutInflater().inflate(R.layout.popup,null);
        popup_bio_text = (EditText) contented.findViewById(R.id.user_bio_popup);
        popup_Save_bio =(Button) contented.findViewById(R.id.popip_savebio);

        dialogBuilder.setView(contented);
        dialog = dialogBuilder.create();
        dialog.show();


        popup_Save_bio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_biography = popup_bio_text.getText().toString();
                if(user_biography.length() > 50) {
                    popup_bio_text.setError("Bio too long");
                }else{
                    reference.child(username).child("bio").setValue(user_biography);
                    t.setText(username+"\n"+user_biography);
                    dialog.dismiss();
                }
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
                    Create_post post;
                    String body = popup_post_body.getText().toString();
                    //String image_url = popup_post_image.getText().toString()
                     Date date = new Date();
                     SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                     String t = (format.format(date)).toString();

                     post = new Create_post(body,"",t);
                    FirebaseDatabase fb = FirebaseDatabase.getInstance();
                    DatabaseReference ref = fb.getReference("Posts").child(username);

                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()) {
                                    maxId = (snapshot.getChildrenCount()) + 1;
                                }
                            ref.child(String.valueOf(maxId)).setValue(post);
                            dialog.dismiss();
                            display_posts();
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

    public void display_posts(){
        LinearLayout lp = (LinearLayout) findViewById(R.id.scroll_posts);
        lp.setOrientation(LinearLayout.VERTICAL);
        lp.removeAllViews();
        DatabaseReference bd = FirebaseDatabase.getInstance().getReference().child("Posts").child(username);
        Query posts =bd.orderByChild(String.valueOf(maxId));
        posts.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Vector<Create_post>post_data;
                post_data = new Vector<Create_post>();
                for(DataSnapshot data:snapshot.getChildren()){
                    String b = data.child("body").getValue(String.class);
                    String t = data.child("time").getValue(String.class);
                    post_data.add(new Create_post(b,"",t));
                }
                for(int i = post_data.size()-1;i>=0;i--){
                    String post_body = post_data.elementAt(i).getBody();
                    String post_time = post_data.elementAt(i).getTime();

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