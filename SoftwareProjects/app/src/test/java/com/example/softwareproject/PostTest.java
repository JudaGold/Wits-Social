package com.example.softwareproject;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PostTest {
    @Test
    public void Post_initialState_validOutput(){
        Post fakePost= new Post("Hello , Its Naazni","-","14:26");

        assertEquals(fakePost.getBody(),"Hello , Its Naazni");
        assertEquals(fakePost.getPost_image_url(),"-");
        assertEquals(fakePost.getTime(),"14:26");

    }

    @Test
    public void Post_postCreation_validOutput(){
        Post fakePost= new Post("Hello , Its Naazni","-","14:26");

        fakePost.setUsername("David The Boy");
        fakePost.setBody("Hello its David");
        fakePost.setTime("00:00");
        fakePost.setPost_image_url("---");

        assertEquals(fakePost.getBody(),"Hello its David");
        assertEquals(fakePost.getPost_image_url(),"---");
        assertEquals(fakePost.getTime(),"00:00");
        assertEquals(fakePost.getUsername(),"David The Boy");
        assertEquals(fakePost.getBody(),"Hello its David");
        assertEquals(fakePost.getTime(),"00:00");
        assertEquals(fakePost.getPost_image_url(),"---");


    }


}