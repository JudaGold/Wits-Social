package com.example.softwareproject;

public class UploadGif {
    private String mImageUrl;

    public UploadGif(){
    //empty constructor needed
    }

    public UploadGif(String imageUrl){
        mImageUrl = imageUrl;
    }

    public String getImageUrl(){
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl){
        mImageUrl = imageUrl;
    }
}
