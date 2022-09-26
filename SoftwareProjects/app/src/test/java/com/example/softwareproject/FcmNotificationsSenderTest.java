package com.example.softwareproject;

import static org.junit.jupiter.api.Assertions.*;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.jupiter.api.Test;

class FcmNotificationsSenderTest {
    //test to check if we get the valid output for the FcmNotificactionSender class.
    @Test
    public void FcmNotificationSender_initialState_validOutput(){
        FcmNotificationsSender fakeTest = new FcmNotificationsSender("Naazni Token","New fake title","Hellow there ",InstrumentationRegistry.getInstrumentation().getContext());
        assertEquals(fakeTest.getClass(),"Naazni Token");
        assertEquals(fakeTest.getClass(),"New fake title");
        assertEquals(fakeTest.getClass(),"Hellow there ");
        assertEquals(fakeTest.getClass(),InstrumentationRegistry.getInstrumentation().getContext());
    }
}