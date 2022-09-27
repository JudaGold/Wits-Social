package com.example.softwareproject;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class
AnalysisTest {

    //test to check if we get the valid output for the text body in analysis class
    @Test
    public void Analysis_initialState_validOutput(){

        Analysis fakebody= new Analysis("this is a fake body text");//creating a fake body text in order to test if the correct output

        assertEquals(fakebody.post_body ,"this is a fake body text");
    }


}