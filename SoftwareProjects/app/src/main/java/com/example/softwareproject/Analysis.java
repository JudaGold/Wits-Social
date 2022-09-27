package com.example.softwareproject;
import android.util.Pair;

import java.net.URL;
import java.util.ArrayList;

public class Analysis {
    String post_body; // The post text by the user

    Analysis(String text){ // Constructor for the class

        this.post_body = text;
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

    public ArrayList Find_link(){ // Find a URL link in a text
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
}




