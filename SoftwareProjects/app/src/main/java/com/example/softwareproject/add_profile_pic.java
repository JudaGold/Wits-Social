package com.example.softwareproject;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class add_profile_pic extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    ImageView UploadImg;
    Button btnSave;
    TextView txtNext;
    String user;

    private Uri mImageUri;

    private StorageReference storageRef = FirebaseStorage.getInstance().getReference("profile_pictures");
    private DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Profile_pic");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_profile_pic);

        btnSave= (Button) findViewById(R.id.btnSave);
        UploadImg=(ImageView) findViewById(R.id.imgAddPic);
        txtNext = (TextView) findViewById(R.id.txtNext);

        Intent intent = getIntent();
            user = intent.getStringExtra("Username");


        UploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileUser();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadFile();
            }
        });

        txtNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(add_profile_pic.this,MainActivity.class);
                intent.putExtra("Username",user);
                startActivity(intent);
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
            Picasso.with(this).load(mImageUri).into(UploadImg);
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
            fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Upload_pic upload = new Upload_pic(taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());
                            String uploadID = user;
                            databaseRef.child(uploadID).setValue(upload);
                            Toast.makeText(add_profile_pic.this, "upload successful", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(add_profile_pic.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }

    }
}