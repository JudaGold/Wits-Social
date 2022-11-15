package com.example.softwareproject;

import static org.junit.Assert.*;

import org.junit.jupiter.api.Test;

import java.text.ParseException;

public class DateComparatorTest {
    DateComparator dc = new DateComparator();

    @Test
    public void compare_comparingDates_firstDateIsLater() throws ParseException {
        Post fakePost = new Post("1","How is everyone?","__","26-03-2000 13:55:55");
        Post fakePost2 = new Post("2","Hello Everyone","__","25-03-2000 13:55:55");

        fakePost.convertDate();
        fakePost2.convertDate();

        assertEquals(-1, dc.compare(fakePost,fakePost2));
    }

    @Test
    public void compare_comparingDates_firstDateIsEarlier() throws ParseException {
        Post fakePost = new Post("1","Hello Everyone","__","24-03-2000 13:55:55");
        Post fakePost2 = new Post("2","How is everyone?","__","25-03-2000 13:55:55");

        fakePost.convertDate();
        fakePost2.convertDate();

        assertEquals(1, dc.compare(fakePost,fakePost2));
    }
}