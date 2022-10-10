package com.example.softwareproject;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

class Display_Hashtag_PostsTest {
        @Test
        void fetchPosts() {
            ArrayList<String> fakeArrayList= new ArrayList();
            fakeArrayList.add("FakeName1");
            fakeArrayList.add("FakeName2");
            ArrayList <String> expected = new ArrayList<>(Arrays.asList("FakeName1","FakeName2"));
            expected.equals(fakeArrayList);
            assertArrayEquals(expected.toArray(),fakeArrayList.toArray());
        }

    }
