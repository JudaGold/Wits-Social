package com.example.softwareproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class main_profile extends AppCompatActivity {
    TextView t;
    EditText popup_bio_text,popup_post_body,popup_post_image;
    Button popup_Save_bio,popup_add_post;
    ImageButton btnadd_post;
    DatabaseReference reference;
    ImageView user_image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_profile);
        Intent intent = getIntent();
        t = (TextView) findViewById(R.id.user_bio);
        String username = intent.getStringExtra("username");
        user_image = (ImageView) findViewById(R.id.user_image);
        set_username(username);
        display_posts(username);


        btnadd_post = (ImageButton) findViewById(R.id.btn_add_post);
        btnadd_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_post(username);
            }
        });

        user_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase fb = FirebaseDatabase.getInstance();
                DatabaseReference ref = fb.getReference("Posts");
                Query checkposts = ref.orderByChild("username").equalTo(username);

                checkposts.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        createPost post = snapshot.child(username).getValue(post.class);
                        Toast.makeText(getApplicationContext(),snapshot.child(username).child("-N0pJiMz_jXcdCspesTy").child("body").toString(),Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });

    }

    public void set_username(String username) {
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
                                add_bio(username);

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
    public void add_bio(String username){
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
    public void add_post(String username){
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
            @Override
            public void onClick(View v) {
                 try {
                    Create_post post;
                    String body = popup_post_body.getText().toString();
                    //String image_url = popup_post_image.getText().toString()
                    post = new Create_post(body,"");
                    FirebaseDatabase fb = FirebaseDatabase.getInstance();
                    DatabaseReference ref = fb.getReference("Posts");
                    ref.child(username).push().setValue(post);
                    dialog.dismiss();

                }catch(Exception e){
                }
            }
        });
    }
    public void display_posts(String username){
        FirebaseDatabase fb = FirebaseDatabase.getInstance();
        DatabaseReference ref = fb.getReference("Posts");
        Query checkposts = ref.orderByChild("username").equalTo(username);

        checkposts.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data:snapshot.child(username).child("post").getChildren()) {
                    String key = data.getKey();
                    System.out.println(key);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

}