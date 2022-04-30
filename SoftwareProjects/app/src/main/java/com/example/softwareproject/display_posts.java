package com.example.softwareproject;


import android.content.Context;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;


public class display_posts extends LinearLayout {
    LinearLayout l;
    ScrollView sv;
    TextView body,time;
    ImageView image;

    public display_posts(Context context) {
        super(context);
        setOrientation(LinearLayout.VERTICAL);
        l = new LinearLayout(context);
        l.setOrientation(LinearLayout.VERTICAL);
        this.addView(l);
        sv = new ScrollView(context);
        l.addView(sv);

        body = new TextView(context);
        time = new TextView(context);

        body.setTextSize(20);
        time.setTextSize(15);
        body.setPadding(30,30,30,30);
        time.setPadding(30,30,30,30);

        LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(1000,150,1.5f);
        ll.setLayoutParams(lp);
        int with = ll.getWidth();

        ll.addView(body);
        ll.addView(time);
        l.addView(ll);
    }

    public void populate(DataSnapshot data){
        body.setText(data.child("body").getValue(String.class));
        time.setText(data.child("time").getValue(String.class));
    }



}
