package com.example.softwareproject;

import android.os.Build;
import androidx.annotation.RequiresApi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RequiresApi(api = Build.VERSION_CODES.O)
public class Post
{
    private String ID,username,body,post_image_url,time,  num_of_replies = "";
    private Date date;// this will store the actual date of the post.

    public Post(String ID,String body, String post_image_url, String time) {
        //Details on the page
        this.ID = ID;
        this.time= time;
        this.body = body;
        this.post_image_url = post_image_url;
    }
    public Post(String ID,String username,String body, String post_image_url, String time) {
        //Details on the page
        this.ID = ID;
        this.username = username;
        this.time= time;
        this.body = body;
        this.post_image_url = post_image_url;
    }



    //Line 27-76: get and set functions for each detail on the post
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getPost_image_url() {
        return post_image_url;
    }

    public void setPost_image_url(String post_image_url) {
        this.post_image_url = post_image_url;
    }

    public String getID() {
        return ID;
    }

   // public void setID(String ID){this.ID = ID; }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNum_of_replies() {
        return num_of_replies;
    }

    public void setNum_of_replies(String num_of_replies) {
        this.num_of_replies = num_of_replies;
    }


    // this method will convert the string "time" to a date object.
    public void convertDate() throws ParseException {
        try{
            this.date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(this.time);
        }catch(Exception c){}

    }

    public Date getDate() {
        return date;
    }

}
// this class will compare the dates and times of the posts.
class DateComparator implements Comparator<Post> {
    // override the compare() method
    @RequiresApi(api = Build.VERSION_CODES.O)
    public int compare(Post p1, Post p2) {
        // if the first post's date and time is later than the second post's date and time
        try {
            if (p1.getDate().compareTo(p2.getDate()) > 0) {
                return -1;
            } else {
                return 1;
            }
        } catch (Exception c) {
        }

    return 0;}
}
