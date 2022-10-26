package com.example.softwareproject;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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

import java.io.File;
import java.io.FileWriter;

public class Show_Profile_Details extends AppCompatActivity
{
    private static final int PICK_IMAGE_REQUEST = 1;
    //Declarations of variable
    TextView UserName, PostCount;
    EditText bio, fullName, PhoneNumber,EmailAddress;
    String username;
    ImageView profile_pic;
    long maxId = 0;
    Button btnAddNewPic, btnSave2, logOut,DownloadInfoBtn,DeleteProfileBtn, BlockedUsersBtn;
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
        BlockedUsersBtn = (Button) findViewById(R.id.BlockUserBtn);
        BlockedUsersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Show_Profile_Details.this, Blocked_Users.class);
                intent.putExtra("Username", username);
                startActivity(intent);
            }
        });

        DownloadInfoBtn = (Button) findViewById(R.id.DwnBtn);//instantiating download button
        DeleteProfileBtn = (Button) findViewById(R.id.DltBtn);//instantiating delete button

        DownloadInfoBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Download_info();//calling function to download users information
             }
        });
        DeleteProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {//calling a prompt
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE://checking if yes is clicked
                                Download_info();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE://checking if no is clicked

                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(Show_Profile_Details.this);
                builder.setMessage("Would you like to first download your data before you delete your profile?")
                        .setPositiveButton("Absolutely", dialogClickListener)
                        .setNegativeButton("of course no.", dialogClickListener).show();//asking user a question
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {//overriding start activity function  so store a picture once take a picture from the camera
        super.onActivityResult(requestCode, resultCode, data);//inheriting from parent

        if ((requestCode == PICK_IMAGE_REQUEST) && (resultCode == RESULT_OK)//checking if camera has provision to be used
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

    void Download_info(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)  == PackageManager.PERMISSION_GRANTED){//checking for perission.
            create_file();//function to create all 3 text files
        }else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},100);
        }
    }

    private void create_file(){
        String root = "/Download/"+username +  "'s WitsSocial profile info";//root path to store data

        try{
            File file = new File(Environment.getExternalStorageDirectory(),root);//creating file
            if(file.exists()){
                file.delete();
            }
            file.mkdirs();//making dir

            write_to_file(file,"Users","Personal_Information.txt");//3 following lines call function to write a specific table from the firebase db to a file in the path dit
            write_to_file(file,"social","Social_Information.txt");
            write_to_file(file,"Posts","Posts_Information.txt");
            Toast.makeText(Show_Profile_Details.this,"Information Successfully downloaded",Toast.LENGTH_SHORT).show();//alerting user their data has been saved on their device

        }catch(Exception e){
            Log.d("Stack","Error creating  file");
        }
    }
    void write_to_file(File root,String dir,String path) {
        try {
            File textfile = new File(root,path);//creating to new file to write to
            if(textfile.exists()){
                textfile.delete();
            }
            FileWriter writer = new FileWriter(textfile);//creating textfile

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(dir).child(username);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    if (snapshot.exists()) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {//iterating through the table in firebase
                            writer.append(dataSnapshot.toString() + "\n");//writing data to file from snapshot
                        }
                    }
                    writer.flush();
                    writer.close();//closing file
                } catch (Exception e) {
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }catch (Exception r) {
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100 && (grantResults.length > 0)
                && (grantResults[0] == PackageManager.PERMISSION_GRANTED)){//checking if app has permission to save and write to a file on the device
            create_file();
        }
    }
}
