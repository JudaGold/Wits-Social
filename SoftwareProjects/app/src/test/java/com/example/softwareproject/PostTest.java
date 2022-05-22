package com.example.softwareproject;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PostTest {
    @Test
    public void PostTest(){
        Post fakePost= new Post("Hello , Its Naazni","-","14:26");
        fakePost.setUsername("Naazni Jagdew");
        fakePost.setBody("Hello , Its Naazni");
        fakePost.setTime("14:26");
        fakePost.setPost_image_url("-");

        assertEquals(fakePost.getBody(),"Hello , Its Naazni");
        assertEquals(fakePost.getPost_image_url(),"-");
        assertEquals(fakePost.getTime(),"14:26");
        assertEquals(fakePost.getUsername(),"Naazni Jagdew");
        assertEquals(fakePost.getBody(),"Hello , Its Naazni");
        assertEquals(fakePost.getTime(),"14:26");
        assertEquals(fakePost.getPost_image_url(),"-");

    }


}