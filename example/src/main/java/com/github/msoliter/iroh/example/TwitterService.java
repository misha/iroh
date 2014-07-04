package com.github.msoliter.iroh.example;

import java.util.List;

import twitter4j.Status;
import twitter4j.TwitterException;

import com.github.msoliter.iroh.container.annotations.Autowired;

public class TwitterService {

    @Autowired(lazy = true)
    private TwitterRetrievalService retrievalService;

    private final String id;
    
    public TwitterService(String id) {
        this.id = id;
    }
    
    public List<Status> search(String text) throws TwitterException {
        return retrievalService.search("@" + id + ":" + text); 
    }
    
    public int followerCount() throws TwitterException {
        return retrievalService.followers(id).getIDs().length;
    }
}
