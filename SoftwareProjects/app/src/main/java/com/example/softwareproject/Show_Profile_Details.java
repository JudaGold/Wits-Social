package com.example.softwareproject;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class Show_Profile_Details extends AppCompatActivity
{
    private static final int PICK_IMAGE_REQUEST = 1;
    //Declarations of variable
    TextView UserName, PostCount;
    EditText bio, fullName, PhoneNumber,EmailAddress;
    String username;
    ImageView profile_pic;
    long maxId = 0;
    Button btnAddNewPic, btnSave2, logOut;
    Field_Validations fv;

    private Uri mImageUri;

    private StorageReference storageRef = FirebaseStorage.getInstance().getReference("profile_pictures");
    private StorageTask mUploadTask;
    DatabaseReference bd = FirebaseDatabase.getInstance().getReference("Users");
    DatabaseReference bd2 = FirebaseDatabase.getInstance().getReference("Posts");

    FirebaseDatabase fb;
    DatabaseReference Gdb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Assign variables
        setContentView(R.layout.show_profile_details);
        fv = new Field_Validations();
        Intent intent = getIntent();
        username = intent.getStringExtra("Username");

        //Assign varibles
        profile_pic = (ImageView) findViewById(R.id.profile_pic);
        UserName = (TextView) findViewById(R.id.UserName_txt);
        PostCount = (TextView) findViewById(R.id.PostCount_txt);
        fullName = (EditText) findViewById(R.id.full_name);
        PhoneNumber = (EditText) findViewById(R.id.phone_number);
        EmailAddress = (EditText) findViewById(R.id.email_address_update);
        bio = (EditText) findViewById(R.id.BioText);
        btnAddNewPic = (Button) findViewById(R.id.btnAddNewPic);
        btnSave2 = (Button) findViewById(R.id.btnSave2);
        logOut = findViewById(R.id.logOut);


        Query getUserInfo = bd.orderByChild("username").equalTo(username);
        getUserInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String imageUrl = snapshot.child(username).child("mImageUrl").getValue(String.class);
                    if (!imageUrl.equalsIgnoreCase(""))
                    {
                        Picasso.get().load(imageUrl).into(profile_pic);
                    }

                    UserName.setText(username);

                    bio.setText(snapshot.child(username).child("bio").getValue(String.class));

                    fullName.setText(snapshot.child(username).child("name").getValue(String.class));

                    PhoneNumber.setText(snapshot.child(username).child("phoneNumber").getValue(String.class));

                    EmailAddress.setText(snapshot.child(username).child("email").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });

        Query getPostCount = bd2.child(username).orderByChild(String.valueOf(maxId));
        getPostCount.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot data : snapshot.getChildren()) {
                        maxId++;
                    }
                    PostCount.setText(Long.toString(maxId));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });

        btnSave2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Assignment variables
                String email = EmailAddress.getText().toString();
                String number  = PhoneNumber.getText().toString();
                String name = fullName.getText().toString();
                String userBio = bio.getText().toString();

                //Declare and assign variables
                boolean completed = completed();
                boolean validEmail = fv.check_email(email,EmailAddress);
                boolean validNumber = fv.Valid_number(number,PhoneNumber);
                boolean validBio = fv.bioValidation(userBio,bio);

                if (completed  && validEmail && validNumber && validBio)
                {
                    //If true, update details on the screen
                    bd.child(username).child("bio").setValue(userBio);
                    bd.child(username).child("email").setValue(email);
                    bd.child(username).child("name").setValue(name);
                    bd.child(username).child("phoneNumber").setValue(number);

                    //If user clicks the button again, return the message in line 'Upload in progress'
                    if (mUploadTask != null && mUploadTask.isInProgress()) {
                        Toast.makeText(Show_Profile_Details.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                    } else {
                        uploadFile();
                    }
                }
            }
        });

        //Switch to file selector after clicking the add new picture
        btnAddNewPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileUser();
            }
        });

        //Click listener for logout button
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = getSharedPreferences("checkBox", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("remember","false");
                editor.apply();

                Intent intent = new Intent(Show_Profile_Details.this, Main_Activity.class);
                startActivity(intent);
            }
        });

    }

    //Function to open the file selector
    private void openFileUser(){
        Intent intent2 = new Intent();
        intent2.setType("image/*");
        intent2.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent2, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == PICK_IMAGE_REQUEST) && (resultCode == RESULT_OK)
                && (data != null) && (data.getData() != null)) {
            mImageUri = data.getData();
            Picasso.get().load(mImageUri).into(profile_pic);
        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    public boolean completed(){
        boolean key = true;
        if(TextUtils.isEmpty(EmailAddress.getText().toString())){
            EmailAddress.setError("Please enter in an email address");
            key =  false;
        }
        if(TextUtils.isEmpty(PhoneNumber.getText().toString())){
            PhoneNumber.setError("Please enter in a phone number");
            key =  false;
        }
        if(TextUtils.isEmpty(fullName.getText().toString())){
            fullName.setError("Please enter in your full name");
            key =  false;
        }
        return key;
    }

    private void uploadFile(){
        if (mImageUri != null){
            StorageReference fileReference = storageRef.child(System.currentTimeMillis() + "." + getFileExtension(mImageUri));
            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>(){

                                @Override
                                public void onSuccess(Uri uri) {
                                    bd.child(username).child("mImageUrl").setValue(uri.toString());
                                    Toast.makeText(Show_Profile_Details.this, "Saved successful", Toast.LENGTH_LONG).show();

                                    Intent intent= new Intent(Show_Profile_Details.this, user_display.class);
                                    intent.putExtra("username", username);
                                    startActivity(intent);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Show_Profile_Details.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

        } else {
            Intent intent= new Intent(Show_Profile_Details.this, user_display.class);
            intent.putExtra("username", username);
            startActivity(intent);
        }

    }
}
