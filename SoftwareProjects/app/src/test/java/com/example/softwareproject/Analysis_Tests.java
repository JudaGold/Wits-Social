package com.example.softwareproject;

import static com.example.softwareproject.Analysis.valid_URL;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import android.app.Activity;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;
import android.util.Pair;

import org.mockito.Mock;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;

@RunWith(MockitoJUnitRunner.class)
public class Analysis_Tests {
    @Mock
    Activity mockActivity;

    Analysis analysis = new Analysis();

    @Test
    public void valid_URL_checkingURL_validURL() {
        String fakeURL = "https://courses.ms.wits.ac.za/moodle/login/index.php";

        assertTrue(valid_URL(fakeURL));
    }

    @Test
    public void valid_URL_checkingURL_invalidURL() {
        String fakeURL = "";

        assertFalse(valid_URL(fakeURL));
    }

    @Test
    public void Find_link_findingLink_foundLink()
    {
        String fake_post_body = "Check out " +
                "https://courses.ms.wits.ac.za/moodle/login/index.php my friend";
        ArrayList<Pair<Integer,Integer>> fake_pair_indexes;

        fake_pair_indexes = analysis.Find_link(fake_post_body);

        assertEquals(1, fake_pair_indexes.size());
    }

    @Test
    public void Find_link_findingLink_notFoundLink()
    {
        String fake_post_body = "Check out the moodle website my friend";
        ArrayList<Pair<Integer,Integer>> fake_pair_indexes;

        fake_pair_indexes = analysis.Find_link(fake_post_body);

        assertEquals(0, fake_pair_indexes.size());
    }

//    @Test
//    public void create_Link()
//    {
//        SpannableString fake_spannable_string_link = new SpannableString("Check out " +
//                "https://courses.ms.wits.ac.za/moodle/login/index.php my friend");
//
////        fake_spannable_string_link = analysis.Create_Link(mockActivity, "Check out " +
////                "https://courses.ms.wits.ac.za/moodle/login/index.php my friend");
//
//        fake_spannable_string_link.setSpan(null,10,61, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//        assertEquals(10, fake_spannable_string_link.getSpanStart(null));
//        assertEquals(61, fake_spannable_string_link.getSpanEnd(null));
//    }
}