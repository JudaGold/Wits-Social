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
    LinearLayout l;
    ScrollView sv;
    TextView body,time;
    ImageView image;
    Typeface typeface;


    public Display_Posts(Context context) {
        super(context);
        typeface = ResourcesCompat.getFont(context,R.font.lato_regular);
        setOrientation(LinearLayout.VERTICAL);
        l = new LinearLayout(context);
        l.setOrientation(LinearLayout.VERTICAL);
        this.addView(l);
        sv = new ScrollView(context);
        l.addView(sv);

        body = new TextView(context);
        time = new TextView(context);
        image = new ImageView(context);

        body.setTextSize(20);
        time.setTextSize(15);
        body.setPadding(30,30,30,30);
        time.setPadding(30,30,30,30);

        LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(1000,5000);
        ll.setLayoutParams(lp);
        int with = ll.getWidth();

        body.setTypeface(typeface);
        time.setTypeface(typeface);

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
