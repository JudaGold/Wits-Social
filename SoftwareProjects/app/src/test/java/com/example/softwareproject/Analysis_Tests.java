package com.example.softwareproject;

import static com.example.softwareproject.Analysis.valid_URL;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import android.app.Activity;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;

import androidx.core.util.Pair;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class Analysis_Tests {
    // Initialising the Analysis object
    Analysis analysis = new Analysis();

    // Testing if valid_URL returns true for a valid url
    @Test
    public void valid_URL_checkingURL_validURL() {
        // Creating a fake valid URL
        String fakeURL = "https://courses.ms.wits.ac.za/moodle/login/index.php";

        // Verifying the return value
        assertTrue(valid_URL(fakeURL));
    }

    // Testing if valid_URL returns false for an invalid url
    @Test
    public void valid_URL_checkingURL_invalidURL() {
        // Creating a fake invalid URL
        String fakeURL = "";

        // Verifying the return value
        assertFalse(valid_URL(fakeURL));
    }

    // Testing if Find_link finds url link in a text when there is a url link
    @Test
    public void Find_link_findingLink_foundLink()
    {
        // Creating a fake text
        String fake_post_body = "Check out " +
                "https://courses.ms.wits.ac.za/moodle/login/index.php my friend";
        ArrayList<Pair<Integer, Integer>> fake_pair_indexes; /* Declaring an ArrayList of pairs of
                                                                indexes that represent the url link
                                                                in the fake text*/

        // Calls the method to find a link
        fake_pair_indexes = analysis.Find_link(fake_post_body);

        // Verifying if the size of the ArrayList is one then it found a link
        assertEquals(1, fake_pair_indexes.size());
    }

    // Testing if Find_link does not find url link in a text when there is no url link
    @Test
    public void Find_link_findingLink_notFoundLink()
    {
        // Creating a fake text
        String fake_post_body = "Check out the moodle website my friend";
        ArrayList<Pair<Integer, Integer>> fake_pair_indexes; /* Declaring an ArrayList of pairs of
                                                                indexes that represent the url link
                                                                in the fake text*/

        // Calls the method to find a link
        fake_pair_indexes = analysis.Find_link(fake_post_body);

        // Verifying if the size of the ArrayList is zero then no link was found
        assertEquals(0, fake_pair_indexes.size());
    }
}