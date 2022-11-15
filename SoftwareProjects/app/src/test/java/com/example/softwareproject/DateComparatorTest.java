package com.example.softwareproject;

import static org.junit.Assert.*;

import org.junit.jupiter.api.Test;

import java.text.ParseException;

public class DateComparatorTest {
    // Initialising the DateComparator object
    DateComparator dc = new DateComparator();

    /* Testing if compare returns -1 when the date of the first post is later then the second's
       post's date */
    @Test
    public void compare_comparingDates_firstDateIsLater() throws ParseException {
        // Initialising the fake Posts objects
        Post fakePost = new Post("1","How is everyone?","__","26-03-2000 13:55:55");
        Post fakePost2 = new Post("2","Hello Everyone","__","25-03-2000 13:55:55");

        // Converting the string time to Date objects
        fakePost.convertDate();
        fakePost2.convertDate();

        // Verifying the return value
        assertEquals(-1, dc.compare(fakePost,fakePost2));
    }

    /* Testing if compare returns 1 when the date of the first post is earlier then the second's
      post's date */
    @Test
    public void compare_comparingDates_firstDateIsEarlier() throws ParseException {
        // Initialising the fake Posts objects
        Post fakePost = new Post("1","Hello Everyone","__","24-03-2000 13:55:55");
        Post fakePost2 = new Post("2","How is everyone?","__","25-03-2000 13:55:55");

        // Converting the string time to Date objects
        fakePost.convertDate();
        fakePost2.convertDate();

        // Verifying the return value
        assertEquals(1, dc.compare(fakePost,fakePost2));
    }
}