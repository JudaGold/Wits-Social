package com.example.softwareproject;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class Add_Profile_Pic extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    // Declarations of variables
    ImageView UploadImg;    // Image view for profile picture
    Button btnSave;         // Button to save bio and profile pic
    String username;        // User's username
    EditText bio;           // User's bio
    Field_Validations fv;   // Object for field validations class
    private Uri mImageUri;  // Profile picture's uri
    private StorageReference storageRef =
            FirebaseStorage.getInstance().getReference("profile_pictures");
                            // Reference to the firebase storage with profile pictures
    private DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Users");
                            // Reference to the User's table in the firebase database
    private StorageTask mUploadTask;
                            // Task for uploading the profile picture in the database and storage

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialising of variables
        setContentView(R.layout.activity_add_profile_pic);
        btnSave= (Button) findViewById(R.id.btnSave);
        UploadImg=(ImageView) findViewById(R.id.imgAddPic);
        bio = (EditText) findViewById(R.id.user_bio2);
        fv = new Field_Validations();

        Intent intent = getIntent();
            username = intent.getStringExtra("Username");
                            // Get the username from sign up activity

        UploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileUser();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userBio = bio.getText().toString();
                            // Get the user's bio
                boolean bioValid = fv.bioValidation(userBio, bio);
                            // Validating the user's bio

                if (bioValid) {
                    databaseRef.child(username).child("bio").setValue(userBio);
                            // Setting the user's bio in the database

                    if (mUploadTask != null && mUploadTask.isInProgress()) {
                        Toast.makeText(Add_Profile_Pic.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                            // Displaying message when the upload is progress
                    } else {
                        uploadFile();
                    }
                }
            }
        });
    }

    // This method will open a file in the phone
    private void openFileUser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // This method will load the chosen image onto the image view
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == PICK_IMAGE_REQUEST) && (resultCode == RESULT_OK)
                && (data != null) && (data.getData() != null)) {
            mImageUri = data.getData();
            Picasso.get().load(mImageUri).into(UploadImg);
        }
    }

    // This method get the photo's extension
    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    // This method will upload the photo to the file storage and the photo's uri to the database
    private void uploadFile(){
        if (mImageUri != null){
            StorageReference fileReference = storageRef.child(System.currentTimeMillis() + "." + getFileExtension(mImageUri));
                            // Creating a reference for the photo
            mUploadTask = fileReference.putFile(mImageUri)
                            // Uploading the photo to the file storage
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>(){

                                @Override
                                public void onSuccess(Uri uri) {
                                    databaseRef.child(username).child("mImageUrl").setValue(uri.toString());
                                    // Set the image's uri in the database
                                    Toast.makeText(Add_Profile_Pic.this, "Saved successful", Toast.LENGTH_LONG).show();
                                    // Showing a message when the photo is done uploading

                                    Intent intent= new Intent(Add_Profile_Pic.this, Main_Activity.class);
                                    intent.putExtra("Username", username);
                                    startActivity(intent);
                                    // This will redirect the user to the Login screen
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Add_Profile_Pic.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            // Shows an error when uploading the photo fails
                        }
                    });

        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
            // Shows a message when no file photo is selected
        }

    }
}