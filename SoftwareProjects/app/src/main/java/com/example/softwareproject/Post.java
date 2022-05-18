package com.example.softwareproject;

import android.os.Build;
import androidx.annotation.RequiresApi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RequiresApi(api = Build.VERSION_CODES.O)
public class Post
{
    private String username,body,post_image_url,time;
    private Date date;// this will store the actual date of the post

    public Post(String body, String post_image_url, String time) {
        this.time= time;
        this.body = body;
        this.post_image_url = post_image_url;
    }

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


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    // this method will convert the string "time" to a date object
    public void convertDate() throws ParseException {
        this.date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(this.time);
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}

// this class will compare the dates and times of the posts
class DateComparator implements Comparator<Post> {
    // override the compare() method
    @RequiresApi(api = Build.VERSION_CODES.O)
    public int compare(Post p1, Post p2)
    {
        // if the first post's date and time is later than the second post's date and time
        if (p1.getDate().compareTo(p2.getDate()) > 0)
        {
            return -1;
        }
        else
        {
            return 1;
        }
    }
}
