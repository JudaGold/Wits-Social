package com.example.softwareproject;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;

class Post_Test {

    @Test
    public void Post_initialState_validOutput(){
        // Creating a post
        // Initializing the post
        Post fakePost= new Post("1","Hello Everyone","__","25-03-2000 13:55:55");

        // Verifying the post's get methods outputs
        assertEquals(fakePost.getBody(),"Hello Everyone");
        assertEquals(fakePost.getPost_image_url(),"__");
        assertEquals(fakePost.getTime(),"25-03-2000 13:55:55");

    }

    @Test
    public void Post_postCreation_validOutput(){
        // Creating a post
        Post fakePost= new Post("1","Hello Everyone","__","25-03-2000 13:55:55");

        // Setting the post's details
        fakePost.setUsername("David The Boy");
        fakePost.setBody("Hello its David");
        fakePost.setTime("00:00");
        fakePost.setPost_image_url("---");

        // Verifying the post's get methods outputs
        assertEquals(fakePost.getBody(),"Hello its David");
        assertEquals(fakePost.getPost_image_url(),"---");
        assertEquals(fakePost.getTime(),"00:00");
        assertEquals(fakePost.getUsername(),"David The Boy");

    }
    @Test
    public void DateComparator_initialState_dateComparatorOutput() throws ParseException {
        Post fakePost= new Post("1","Hello Everyone","__","25-03-2000 13:55:55");
        Post fakePost2=new Post("1","Hello Everyone","__","25-03-2000 13:55:55");
        fakePost.convertDate();
        fakePost2.convertDate();

        fakePost.getDate().compareTo(fakePost2.getDate());
        
    }




}