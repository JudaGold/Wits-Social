package com.example.softwareproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class Forgot_Password extends AppCompatActivity {
    // Declaration of variables
    EditText emailfield,p1,p2; // Text views for the email and the new password
    Button btn,cbtn; // Buttons to save the new password and enter the email address to the app
    LinearLayout l,ll;
    String email;
    FirebaseDatabase fb,rootnode,temp;
    DatabaseReference Gdb;
    Field_Validations fv = new Field_Validations();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        l = (LinearLayout) findViewById(R.id.mainlayout);
        ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        l.addView(ll);


        cbtn = new Button(this);
        cbtn.setText("enter");
        emailfield = new EditText(this);
        emailfield.setHint("enter email address.");
        ll.addView(emailfield);
        ll.addView(cbtn);

        fb = FirebaseDatabase.getInstance();
        Gdb = fb.getReference("Users");

        cbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    email = emailfield.getText().toString();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                    Query checkemail = ref.orderByChild("email").equalTo(email);
                    checkemail.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                l.removeView(ll);
                                p1 = new EditText(Forgot_Password.this);
                                p2 = new EditText(Forgot_Password.this);
                                btn = new Button(Forgot_Password.this);

                                p1.setHint("Enter in a new password");
                                p1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                p1.setSelection(p1.getText().length());
                                p2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                p2.setSelection(p2.getText().length());
                                p2.setHint("Re enter in you new password");
                                btn.setText("Change Password");
                                l.addView(p1);
                                l.addView(p2);
                                l.addView(btn);

                                btn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String newp1, newp2;
                                        newp1 = p1.getText().toString();
                                        newp2 = p2.getText().toString();

                                        if (fv.valid_password(newp1, p1)) {
                                            if (fv.passwords_match(newp1, newp2, p2)) {
                                                try {
                                                    for (DataSnapshot data : snapshot.getChildren()) {
                                                        Object temp = data.getValue();
                                                        for (String i : temp.toString().split(",")) {
                                                            if (i.contains("username")) {
                                                                if (i.endsWith("}")) {
                                                                    i = i.substring(10, i.length() - 1);
                                                                }
                                                                ref.child(i).child("password").setValue(newp1);
                                                                Intent intent = new Intent(Forgot_Password.this, Main_Activity.class);
                                                                l.removeAllViews();
                                                                startActivity(intent);
                                                                break;
                                                            }

                                                        }
                                                    }
                                                } catch (Exception e) {
                                                }
                                            }
                                        }

                                    }
                                });

                            } else {
                                emailfield.setError("Account does not exist");
                            }
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
}
