package com.example.softwareproject;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;

class Post_Test {

    // Testing the constructor of the Post class
    @Test
    public void Post_initialState_validOutput(){
        // Initializing the post
        Post fakePost = new Post("1","Hello Everyone","__","25-03-2000 13:55:55");

        // Verifying the post's get methods outputs
        assertEquals("1", fakePost.getID());
        assertEquals("Hello Everyone", fakePost.getBody());
        assertEquals("__", fakePost.getPost_image_url());
        assertEquals("25-03-2000 13:55:55", fakePost.getTime());
    }

    // Testing the constructor of the Post class
    @Test
    public void Post_initialStateWithUsername_validOutput(){
        // Initializing the post
        Post fakePost= new Post("1","Dave","Hello Everyone","__","25-03-2000 13:55:55");

        // Verifying the post's get methods outputs
        assertEquals("1", fakePost.getID());
        assertEquals("Hello Everyone" ,fakePost.getBody());
        assertEquals("__", fakePost.getPost_image_url());
        assertEquals("25-03-2000 13:55:55", fakePost.getTime());
        assertEquals("Dave", fakePost.getUsername());
    }

    // Testing the set methods of the Post class
    @Test
    public void Post_postCreation_validOutput(){
        // Initializing the post
        Post fakePost= new Post("1","Hello Everyone","__","25-03-2000 13:55:55");

        // Setting the post's details
        fakePost.setUsername("Dave");
        fakePost.setNum_of_replies("2");

        // Verifying the post's get methods outputs
        assertEquals("1", fakePost.getID());
        assertEquals("Hello Everyone" ,fakePost.getBody());
        assertEquals("__", fakePost.getPost_image_url());
        assertEquals("25-03-2000 13:55:55", fakePost.getTime());
        assertEquals("Dave", fakePost.getUsername());
        assertEquals("2", fakePost.getNum_of_replies());
    }
}