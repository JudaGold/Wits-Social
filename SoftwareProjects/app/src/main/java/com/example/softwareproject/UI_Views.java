package com.example.softwareproject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;

public class UI_Views {
    Analysis analysis;
    public TextView createUsernameTextView(Context context,String str) {
        TextView user = new TextView(context);
        user.setTextSize(20);
        user.setText(str);
        user.setPadding(30, 30, 30, 30);
        user.setTextColor(Color.parseColor("#FF47FAF3"));
        user.setGravity(Gravity.LEFT);
        return user;

    }

    public TextView createErrorTextView(Context context,String str) {
        TextView user = new TextView(context);
        user.setTextSize(20);
        user.setHeight(200);
        user.setBackgroundColor(Color.parseColor("#403D3D"));
        user.setText(str);
        user.setPadding(30, 30, 30, 30);
        user.setTextColor(Color.parseColor("white"));
        user.setGravity(Gravity.CENTER);
        return user;

    }

    public ImageView createImageView(Context context,Activity activity,String str) {
        ImageView imageView = new ImageView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1100);
        params.gravity = Gravity.LEFT; //sets the image at the centre
        params.setMargins(0, 40, 0, 50);
        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        this.getImage(activity,str,imageView);
        return imageView;
    }
    private void getImage(Activity activity,String URL, ImageView image){
        Glide.with(activity).load(URL).into(image); /*gets image from the internet and adds
                                                                                            it to imageView*/
    }


    public TextView createBodyTextView(Context context,Activity activity,String str) {
        analysis = new Analysis();
        TextView body = new TextView(context);
        SpannableString sp = analysis.Create_Link(activity,str);
        body.setText(sp);
        body.setTextSize(20);
        body.setTextColor(Color.parseColor("white"));
        body.setPadding(30, 30, 30, 30);
        body.setMovementMethod(LinkMovementMethod.getInstance());

        return body;
    }

    public TextView createTimeTextView(Context context, String str) {
        TextView time = new TextView(context);
        time.setText(str);
        time.setGravity(Gravity.RIGHT);
        time.setTextSize(11);
        time.setTextColor(Color.parseColor("white"));
        time.setPadding(0, 5, 20, 0);
        return time;
    }

    public LinearLayout createPostLayout(Context context) {
        LinearLayout post = new LinearLayout(context);
        post.setOrientation(LinearLayout.VERTICAL);
        post.setBackground(ContextCompat.getDrawable(context, R.drawable.post_layout));
        post.setPadding(30, 30, 20, 30);
        return post;
    }

    public Space addSpace(Context context) {
        Space space = new Space(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 50);
        space.setLayoutParams(params);
        return space;
    }

    public LinearLayout createHorizontalLayout(Context context) {
        LinearLayout horizontalLayout = new LinearLayout(context);
        horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
        horizontalLayout.setHorizontalGravity(Gravity.RIGHT);
        horizontalLayout.setPadding(30, 10, 20, 20);
        return horizontalLayout;
    }
}
