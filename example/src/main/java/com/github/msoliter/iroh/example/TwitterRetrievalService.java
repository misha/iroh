package com.github.msoliter.iroh.example;

import java.util.List;

import twitter4j.IDs;
import twitter4j.Query;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import com.github.msoliter.iroh.container.annotations.Autowired;
import com.github.msoliter.iroh.container.annotations.Component;

@Component
public class TwitterRetrievalService {

    @Autowired
    private Twitter twitter;

    public List<Status> search(String query) throws TwitterException {
        return twitter.search(new Query(query)).getTweets();
    }
    
    public IDs followers(String id) throws TwitterException {
        return twitter.getFollowersIDs(id, -1);
    }
}
