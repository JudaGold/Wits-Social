package com.example.softwareproject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UI_Views {
    Analysis analysis;
    public TextView createUsernameTextView(Context context,String str) {
        TextView user = new TextView(context);
        user.setTextSize(21);
        user.setText(str);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(852, ViewGroup.LayoutParams.WRAP_CONTENT);
        user.setLayoutParams(params);
        user.setTypeface(null,Typeface.BOLD);
        user.setPadding(10, 5, 30, 0);
        //user.setTextColor(Color.parseColor("#FF47FAF3"));
        user.setTextColor(Color.parseColor("white"));
        user.setGravity(Gravity.LEFT);
        return user;
    }

    public TextView createNumOfReplies(Context context, String num_of_replies) {
        TextView textView = new TextView(context);
        textView.setText(num_of_replies);
        textView.setTextSize(10);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(80, 80);
        textView.setLayoutParams(params);
        params.bottomMargin = 4;
        textView.setPadding(0, 0, 0, 15);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.parseColor("white"));
        textView.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_round_chat_bubble_outline_24));
        return textView;
    }


    public TextView createBodyTextViewHashtag(Context context, SpannableString str) {
        TextView body = new TextView(context);
        body.setText(str);
        body.setTextSize(20);
        body.setMovementMethod(LinkMovementMethod.getInstance());
        body.setTextColor(Color.parseColor("white"));
        body.setPadding(30, 30, 30, 30);
        body.setMovementMethod(LinkMovementMethod.getInstance());
        return body;
    }

    public ImageView previewImageView(Context context){
        ImageView imgView = new ImageView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1100);
        params.gravity = Gravity.CENTER_HORIZONTAL; //sets the image at the centre
        params.setMargins(10, 20, 10, 20);
        imgView.setLayoutParams(params);
        return imgView;
    }

    public Button createButton(Context context, String media){
        Button button = new Button(context);
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(350, 120);
        buttonParams.gravity = Gravity.CENTER_HORIZONTAL; //sets the image at the centre
        buttonParams.setMargins(0,10,0,50);
        button.setLayoutParams(buttonParams);
        button.setTextColor(Color.parseColor("white"));
        button.setBackgroundResource(R.drawable.button_shape_square);
        button.setText("Upload " + media);

        return button;
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

    public VideoView createVideoView(Context context) {
        VideoView videoView = new VideoView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1100);
        params.gravity = Gravity.LEFT; //sets the image at the centre
        params.setMargins(0, 40, 0, 50);
        videoView.setLayoutParams(params);
        //videoView.setScaleType(VideoView.ScaleType.FIT_XY);
        //this.getImage(activity,str,videoView);
        return videoView;
    }

    public ImageView createImageView(Context context,Activity activity,String str) {
        ImageView imageView = new ImageView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1100);
        params.gravity = Gravity.LEFT; //sets the image at the centre
        params.setMargins(0, 40, 0, 50);
        imageView.setLayoutParams(params);
        //imageView.setScaleType(ImageView.ScaleType);
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
        body.setTextSize(18);
        body.setTextColor(Color.parseColor("white"));
        body.setPadding(40, 30, 30, 30);
        body.setMovementMethod(LinkMovementMethod.getInstance());

        return body;
    }

    public TextView createTimeTextView(Context context, String str) {
        TextView time = new TextView(context);
        time.setText(str);
        time.setGravity(Gravity.RIGHT);
        time.setTextSize(11);
        time.setTextColor(Color.parseColor("white"));
        time.setPadding(0, 20, 0, 20);
        return time;
    }

    public LinearLayout createPostLayout(Context context) {
        LinearLayout post = new LinearLayout(context);
        post.setOrientation(LinearLayout.VERTICAL);
        post.setBackground(ContextCompat.getDrawable(context, R.drawable.post_layout));
        post.setPadding(20, 0, 20, 20);
        return post;
    }

    public Space addSpace(Context context) {
        Space space = new Space(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 30);
        space.setLayoutParams(params);
        return space;
    }

    public LinearLayout createHorizontalLayout(Context context) {
        LinearLayout horizontalLayout = new LinearLayout(context);
       /* LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 150);
        horizontalLayout.setLayoutParams(params);*/
        horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
        horizontalLayout.setHorizontalGravity(Gravity.RIGHT);
        horizontalLayout.setPadding(0, 20, 20, 5);
        return horizontalLayout;
    }

}
