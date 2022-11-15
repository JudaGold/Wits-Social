package com.example.softwareproject;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import androidx.core.util.Pair;
import android.view.View;

import androidx.annotation.NonNull;

import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

public class Analysis {
    ClickableSpan clickableSpan;

    Analysis(){ // Constructor for the class
    }

    static boolean valid_URL(String url){ // Checks for a valid URL
        try{
            new URL(url).toURI();
            return true;
        }
        catch(Exception c){
            return false;
        }
    }

    public ArrayList <Pair<Integer,Integer>>Find_link(String post_body){ // Find a URL link in a text
        ArrayList<Pair<Integer,Integer>>data = new ArrayList<>();
        String words[] = post_body.split(" ");
        for(String it:words){
            if(valid_URL(it)) {
                int num = post_body.indexOf(it);
                Pair<Integer, Integer> temp = new Pair<>(num, num + it.length());
                data.add(temp);
            }
        }
        return data;
    }

    @ExcludeFromJacocoGeneratedReport // This is used to exclude the method from the code coverage
    public SpannableString Create_Link(Activity activity,String body){//function to set up link from spannable strings
        ArrayList<Pair<Integer,Integer>>data = new ArrayList<Pair<Integer,Integer>>();
        data = this.Find_link(body);
        SpannableString spannableString = new SpannableString(body);//setting up a spannable string to process links
        if(data.size() > 0) {
            for (Pair it : data) {//iterating through all the links in a given post
                int a = Integer.parseInt("" + it.first);//getting positions of the beginning and end of the link
                int b = Integer.parseInt("" + it.second);//same as above but for end position
                ClickableSpan clickableSpan = new ClickableSpan() {
                    @ExcludeFromJacocoGeneratedReport // This is used to exclude the method from the code coverage
                    @Override
                    public void onClick(@NonNull View widget) {
                        String url = body.substring(a,b).toLowerCase(Locale.ROOT);//getting the exact link from the post
                        Uri uri = Uri.parse(url);//setting up a url from the post
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);//opening up browser to search for the link
                        activity.startActivity(intent);//starting search of link provided by user
                    }

                    @ExcludeFromJacocoGeneratedReport // This is used to exclude the method from the code coverage
                    @Override
                    public void updateDrawState(@NonNull TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setColor(Color.CYAN);
                    }
                };
                spannableString.setSpan(clickableSpan,a,b, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//creating a clickable part of the string for user to press
            }
        }
        return spannableString;
    }
}




