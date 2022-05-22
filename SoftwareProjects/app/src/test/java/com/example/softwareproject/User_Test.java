package com.example.softwareproject;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class User_Test {
    @Test
    public void UserAssignmentTest_ValidAssignment() {
        User fakeUser = new User("Elementrix08",
                "verushannaidoo@gmail.com", "0615805094", "Elementrix",
                "Verushan Naidoo", "Talk is cheap, show me the code", "-");

        assertEquals(fakeUser.getName(), "Verushan Naidoo");
        assertEquals(fakeUser.getUsername(), "Elementrix08");
        assertEquals(fakeUser.getBio(), "Talk is cheap, show me the code");
        assertEquals(fakeUser.getEmail(), "verushannaidoo@gmail.com");
        assertEquals(fakeUser.getPhoneNumber(), "0615805094");
        assertEquals(fakeUser.getPassword(), "Elementrix");
        assertEquals(fakeUser.getmImageUrl(),"-");
    }
}