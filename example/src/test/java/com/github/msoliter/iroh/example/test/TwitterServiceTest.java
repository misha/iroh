package com.github.msoliter.iroh.example.test;

import org.junit.Test;

import twitter4j.TwitterException;

import com.github.msoliter.iroh.example.TwitterService;

public class TwitterServiceTest {

    @Test(expected = IllegalStateException.class)
    public void testRequestWithoutAuthentication() throws TwitterException {
        TwitterService service = new TwitterService("BarackObama");
        System.out.println("Obama has received tweets about DI " + 
            service.search("dependency injection").size() + " times.");
    }
}
