package com.example.softwareproject;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.util.Pair;
import android.view.View;

import androidx.annotation.NonNull;

import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

public class Analysis {
    String post_body; // The post text by the user

    Analysis(){ // Constructor for the class
    }
    Analysis(String post_body){
        this.post_body = post_body;
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

    private ArrayList Find_link(String post_body){ // Find a URL link in a text
        ArrayList<Pair<Integer,Integer>>data = new ArrayList<>();
        String words[] = post_body.split(" ");
        for(String it:words){
            if(valid_URL(it)) {
                int num = post_body.indexOf(it);
                Pair<Integer, Integer> temp = new Pair(num, num + it.length());
                data.add(temp);
            }
        }
        return data;
    }

    public SpannableString Create_Link(Activity activity,String body){//function to set up link from spannable strings
        ArrayList<Pair<Integer,Integer>>data = new ArrayList<Pair<Integer,Integer>>();
        data = this.Find_link(body);
        SpannableString spannableString = new SpannableString(body);
        if(data.size() > 0) {
            for (Pair it : data) {
                int a = Integer.parseInt("" + it.first);
                int b = Integer.parseInt("" + it.second);
                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        String url = body.substring(a,b).toLowerCase(Locale.ROOT);
                        Uri uri = Uri.parse(url);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        activity.startActivity(intent);
                    }
                };
                spannableString.setSpan(clickableSpan,a,b, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return spannableString;
    }
}




