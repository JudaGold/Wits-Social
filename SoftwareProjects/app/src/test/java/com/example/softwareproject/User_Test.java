package com.example.softwareproject;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class User_Test {
    @Test
    public void User_initialState_validOutput() {
        // Creating a user.
        // Initializing the user
        User fakeUser = new User("Elementrix08",
                "verushannaidoo@gmail.com", "0615805094", "Elementrix",
                "Verushan Naidoo", "Talk is cheap, show me the code", "-", "fcm_token");

        // Verifying the user's get methods outputs
        assertEquals(fakeUser.getName(), "Verushan Naidoo");
        assertEquals(fakeUser.getUsername(), "Elementrix08");
        assertEquals(fakeUser.getBio(), "Talk is cheap, show me the code");
        assertEquals(fakeUser.getEmail(), "verushannaidoo@gmail.com");
        assertEquals(fakeUser.getPhoneNumber(), "0615805094");
        assertEquals(fakeUser.getPassword(), "Elementrix");
        assertEquals(fakeUser.getmImageUrl(),"-");
        assertEquals(fakeUser.getFcm_token(),"fcm_token");
    }
    @Test
    public void User_settingUser_validOutput(){
        // Creating a user
        User fakeUser = new User("Naazni",
                "naaznijagdew@gmail.com", "0729119119", "NaazniPassword",
                "Naazni Jagdew", "Hello Sky", "__", "fcm_token");

        // Setting the user's details
        fakeUser.setBio("Hello Rover");
        fakeUser.setUsername("Dave");
        fakeUser.setEmail("david@gmail.com");
        fakeUser.setmImageUrl("___---");
        fakeUser.setName("Dave The Boy");
        fakeUser.setPassword("DavidPassword");
        fakeUser.setPhoneNumber("0729191919");
        fakeUser.setFcm_token("token");

        // Verifying the user's get methods outputs
        assertEquals(fakeUser.getName(), "Dave The Boy");
        assertEquals(fakeUser.getUsername(), "Dave");
        assertEquals(fakeUser.getBio(), "Hello Rover");
        assertEquals(fakeUser.getEmail(), "david@gmail.com");
        assertEquals(fakeUser.getPhoneNumber(), "0729191919");
        assertEquals(fakeUser.getPassword(), "DavidPassword");
        assertEquals(fakeUser.getmImageUrl(), "___---");
        assertEquals(fakeUser.getFcm_token(),"token");
    }


}
