package com.example.softwareproject;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class User_Test {
    @Test
    public void UserTest() {
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
        assertEquals(fakeUser.getName(), "Verushan Naidoo");
        assertEquals(fakeUser.getUsername(), "Elementrix08");
        assertEquals(fakeUser.getBio(), "Talk is cheap, show me the code");
        assertEquals(fakeUser.getEmail(), "verushannaidoo@gmail.com");
        assertEquals(fakeUser.getPhoneNumber(), "0615805094");
        assertEquals(fakeUser.getPassword(), "Elementrix");
        assertEquals(fakeUser.getmImageUrl(),"-");

    }
}