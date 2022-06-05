package com.example.softwareproject;



public class User {
    private String Username;
    private String email;
    private String phoneNumber;
    private String password;
    private String name;
    private String bio;
    private String mImageUrl;

    public String getFcm_token() {
        return fcm_token;
    }

    public void setFcm_token(String fcm_token) {
        this.fcm_token = fcm_token;
    }

    private String fcm_token;

    public User() {
    }

    public User(String username, String email, String phoneNumber, String password, String name, String bio, String mImageUrl, String fcm_token) {
        Username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.name = name;
        this.bio = bio;
        this.mImageUrl = mImageUrl;
        this.fcm_token = fcm_token;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
