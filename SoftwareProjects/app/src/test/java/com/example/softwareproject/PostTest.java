package com.example.softwareproject;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;

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

    }
    @Test
    public void DateComparator_initialState_dateComparatorOutput() throws ParseException {
        Post fakePost= new Post("Hello Everyone","__","25-03-2000 13:55:55");
        Post fakePost2=new Post("Hello Everyone","__","25-03-2000 13:55:55");
        fakePost.convertDate();
        fakePost2.convertDate();

        fakePost.getDate().compareTo(fakePost2.getDate());
        
    }




}