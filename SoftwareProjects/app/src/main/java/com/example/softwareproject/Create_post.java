package com.example.softwareproject;

import android.media.Image;

public class Create_post {
    String username,body,post_image_url;
    Image image_url;


    public Create_post(String body, String post_image_url) {
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
}
