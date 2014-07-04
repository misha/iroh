package com.github.msoliter.iroh.example;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;

import com.github.msoliter.iroh.container.annotations.Component;

@Component
public class TwitterConfiguration {

    @Component
    public Twitter twitter() {
        return TwitterFactory.getSingleton();
    }
}
