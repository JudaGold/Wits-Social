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
 // Decarations of variables
    ImageView UploadImg;
    Button btnSave;
    String username;
    EditText bio;
    Field_Validations fv;

    private Uri mImageUri;

    private StorageReference storageRef = FirebaseStorage.getInstance().getReference("profile_pictures");
    private DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Users");
    private StorageTask mUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_profile_pic);

        btnSave= (Button) findViewById(R.id.btnSave);
        UploadImg=(ImageView) findViewById(R.id.imgAddPic);
        bio = (EditText) findViewById(R.id.user_bio2);
        fv = new Field_Validations();

        Intent intent = getIntent();
            username = intent.getStringExtra("Username");


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
                boolean bioValid = fv.bioValidation(userBio, bio);

                if (bioValid) {
                    databaseRef.child(username).child("bio").setValue(userBio);

                    if (mUploadTask != null && mUploadTask.isInProgress()) {
                        Toast.makeText(Add_Profile_Pic.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                    } else {
                        uploadFile();
                    }
                }
            }
        });
    }

    private void openFileUser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == PICK_IMAGE_REQUEST) && (resultCode == RESULT_OK)
                && (data != null) && (data.getData() != null)) {
            mImageUri = data.getData();
            Picasso.get().load(mImageUri).into(UploadImg);
        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
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
                                    databaseRef.child(username).child("mImageUrl").setValue(uri.toString());
                                    Toast.makeText(Add_Profile_Pic.this, "Saved successful", Toast.LENGTH_LONG).show();

                                    Intent intent= new Intent(Add_Profile_Pic.this, Main_Activity.class);
                                    intent.putExtra("Username", username);
                                    startActivity(intent);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Add_Profile_Pic.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }

    }
}