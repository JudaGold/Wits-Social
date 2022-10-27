package com.example.softwareproject;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;

import com.google.firebase.database.DataSnapshot;

import java.io.IOException;
import java.io.InputStream;


public class Display_Posts extends LinearLayout {
    //Declaration of variables
    LinearLayout l;//layout for post
    ScrollView sv;//scroll view for posts
    TextView body,time;//textview for post body
    ImageView image;//image view to show iages
    Typeface typeface;//typetace var


    public Display_Posts(Context context) {
        //Assignment to the variables
        super(context);
        typeface = ResourcesCompat.getFont(context,R.font.lato_regular);//settiing font for text view
        setOrientation(LinearLayout.VERTICAL);//setting orientation
        l = new LinearLayout(context);//creating new layout
        l.setOrientation(LinearLayout.VERTICAL);//setting orientation
        this.addView(l);//adding view
        sv = new ScrollView(context);//adding scroll view
        l.addView(sv);//adding cscroll

        //Assignment to the variables
        body = new TextView(context);
        time = new TextView(context);
        image = new ImageView(context);

        //Set parameter values
        body.setTextSize(20);
        time.setTextSize(15);
        body.setPadding(30,30,30,30);
        time.setPadding(30,30,30,30);

        //Assignment to the variables
        LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(1000,5000);
        ll.setLayoutParams(lp);
        int with = ll.getWidth();

        body.setTypeface(typeface);
        time.setTypeface(typeface);

        //Add views to the app
        ll.addView(body);
        ll.addView(time);
        ll.addView(image);
        l.addView(ll);
    }

    public void populate(DataSnapshot data){
        body.setText(data.child("body").getValue(String.class));
        time.setText(data.child("time").getValue(String.class));


    }



}
