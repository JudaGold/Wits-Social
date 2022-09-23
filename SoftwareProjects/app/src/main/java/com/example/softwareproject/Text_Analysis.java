package com.example.softwareproject;

import android.util.Pair;
import android.widget.TextView;
import java.net.URL;

import java.util.ArrayList;

public class TextAnalysyis{
    String TextBody;

    TextAnalysyis(String text){
        this.TextBody = text;
    }

    static boolean valid_URL(String url){
            try{
                new URL(url).toURI();
                return true;
            }
            catch(Exception e){
                return false;
            }
        }


    ArrayList<Pair<Integer,Integer>> Find_link(){//function that finds a url
        ArrayList<Pair<Integer,Integer>>data = new ArrayList<>();
        String words[] = TextBody.split(" ");
        for(String it:words){
            if(valid_URL(it)) {
                int num = TextBody.indexOf(it);
                Pair<Integer, Integer> temp = new Pair(num, num + it.length());
                data.add(temp);
            }
            }
        return data;
        }
    }

