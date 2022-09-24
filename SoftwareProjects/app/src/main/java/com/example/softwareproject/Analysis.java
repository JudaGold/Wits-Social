package com.example.softwareproject;
import android.util.Pair;

import java.net.URL;
import java.util.ArrayList;

public class Analysis {
    String post_body;

    Analysis(String text){
        this.post_body = text;
    }

    static boolean valid_URL(String url){
        try{
            new URL(url).toURI();
            return true;
        }
        catch(Exception c){
            return false;
        }
    }

    public ArrayList Find_link(){
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




