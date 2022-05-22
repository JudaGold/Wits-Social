package com.example.softwareproject;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class User_Test {
    @Test
    public void user_initialState_validOutput() {
        User fakeUser = new User("Elementrix08",
                "verushannaidoo@gmail.com", "0615805094", "Elementrix",
                "Verushan Naidoo", "Talk is cheap, show me the code", "-");

        fakeUser.setBio("Talk is cheap, show me the code");
        fakeUser.setUsername("Elementrix08");
        fakeUser.setEmail("verushannaidoo@gmail.com");
        fakeUser.setmImageUrl("-");
        fakeUser.setName("Verushan Naidoo");
        fakeUser.setPassword("Elementrix");
        fakeUser.setPhoneNumber("0615805094");


        assertEquals(fakeUser.getName(), "Verushan Naidoo");
        assertEquals(fakeUser.getUsername(), "Elementrix08");
        assertEquals(fakeUser.getBio(), "Talk is cheap, show me the code");
        assertEquals(fakeUser.getEmail(), "verushannaidoo@gmail.com");
        assertEquals(fakeUser.getPhoneNumber(), "0615805094");
        assertEquals(fakeUser.getPassword(), "Elementrix");
        assertEquals(fakeUser.getmImageUrl(),"-");


    }

    public void user_initialState_validOutputDiff(){
        User fakeUser = new User("Naazni",
                "naaznijagdew@gmail.com", "0729119119", "NaazniPassword",
                "Naazni Jagdew", "Hello Sky", "__");

        fakeUser.setBio("Hello Sky");
        fakeUser.setUsername("Naazni");
        fakeUser.setEmail("naaznijagdew@gmail.com");
        fakeUser.setmImageUrl("__");
        fakeUser.setName("Naazni Jagdew");
        fakeUser.setPassword("NaazniPassword");
        fakeUser.setPhoneNumber("0729119119");

        assertEquals(fakeUser.getName(), "Naazni Jagdew");
        assertEquals(fakeUser.getUsername(), "Naazni");
        assertEquals(fakeUser.getBio(), "Hello Sky");
        assertEquals(fakeUser.getEmail(), "naaznijagdew@gmail.com");
        assertEquals(fakeUser.getPhoneNumber(), "0729119119");
        assertEquals(fakeUser.getPassword(), "NaazniPassword");
        assertEquals(fakeUser.getmImageUrl(), "__");
    }


}