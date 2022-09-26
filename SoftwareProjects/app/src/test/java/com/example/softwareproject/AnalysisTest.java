package com.example.softwareproject;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class
AnalysisTest {

    //test to check if we get the valid output for the text body in analysis class
    @Test
    public void Analysis_initialState_validOutput(){

        Analysis fakebody= new Analysis("this is a fake body text");

        assertEquals(fakebody.post_body ,"this is a fake body text");
    }

//    public void valid_url_initialState_validOutput(){
//        String fakeValidUrl = ("http://example.com");
//        boolean validUrl = Analysis.valid_URL(fakeValidUrl);
//
//    }


}