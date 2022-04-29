package com.example.softwareproject;


import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;




public class display_posts extends LinearLayout {


    private  TextView make,model,price;

    String body;
    public display_posts(Context context) {
        super(context);
        setOrientation(LinearLayout.VERTICAL);

    }


}
