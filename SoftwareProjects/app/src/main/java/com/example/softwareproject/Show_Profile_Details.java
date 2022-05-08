package com.example.softwareproject;

import android.content.ContentResolver;
import android.content.Intent;
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

    TextView UserName;
    EditText bio, fullName, PhoneNumber,EmailAddress;
    String username;
    ImageView profile_pic;
    Button btnAddNewPic, btnSave2;

    private Uri mImageUri;

    private StorageReference storageRef = FirebaseStorage.getInstance().getReference("profile_pictures");
    private StorageTask mUploadTask;
    DatabaseReference bd = FirebaseDatabase.getInstance().getReference("Users");

    FirebaseDatabase fb;
    DatabaseReference Gdb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_profile_details);

        Intent intent = getIntent();
        username = intent.getStringExtra("Username");

        profile_pic = (ImageView) findViewById(R.id.profile_pic);
        UserName = (TextView) findViewById(R.id.UserName_txt);
        fullName = (EditText) findViewById(R.id.full_name);
        PhoneNumber = (EditText) findViewById(R.id.phone_number);
        EmailAddress = (EditText) findViewById(R.id.email_address_update);
        bio = (EditText) findViewById(R.id.BioText);
        btnAddNewPic = (Button) findViewById(R.id.btnAddNewPic);
        btnSave2 = (Button) findViewById(R.id.btnSave2);


        Query getUserInfo = bd.orderByChild("username").equalTo(username);
        getUserInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String imageUrl = snapshot.child(username).child("mImageUrl").getValue(String.class);
                    Picasso.with(Show_Profile_Details.this).load(imageUrl).into(profile_pic);

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

        btnSave2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = EmailAddress.getText().toString();
                String number  = PhoneNumber.getText().toString();
                String name = fullName.getText().toString();
                String userBio = bio.getText().toString();

                boolean completed = completed();
                boolean validEmail = check_email(email);
                boolean validNUmber = Valid_number(number);
                boolean validBio = bioValidation(userBio);

                if (completed  && validEmail && validNUmber && validBio)
                {
                    bd.child(username).child("bio").setValue(userBio);
                    bd.child(username).child("email").setValue(email);
                    bd.child(username).child("name").setValue(name);
                    bd.child(username).child("phoneNumber").setValue(number);

                    if (mUploadTask != null && mUploadTask.isInProgress()) {
                        Toast.makeText(Show_Profile_Details.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                    } else {
                        uploadFile();
                    }
                }
            }
        });

        btnAddNewPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileUser();
            }
        });

    }

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
            Picasso.with(this).load(mImageUri).into(profile_pic);
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

    public boolean check_email(String email){
        if(email.contains("@")){
            return true;
        }
        else {
            EmailAddress.setError("Invalid email address");
            return false;
        }
    }

    public boolean Valid_number(String num){
        if(num.length()!=10){
            PhoneNumber.setError("invalid phone number");
            return false;
        }
        else{
            return true;
        }
    }

    public boolean bioValidation(String userBio)
    {
        boolean bioValid = true;
        if (userBio.length() > 50)
        {
            bio.setError("Bio too long (it needs to be less than 50)");
            bioValid = false;
        }
        return bioValid;
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

                                    Intent intent= new Intent(Show_Profile_Details.this,MainActivity.class);
                                    intent.putExtra("Username", username);
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
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }

    }
}
