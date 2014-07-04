package com.github.msoliter.iroh.examples.network;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.github.msoliter.iroh.container.annotations.Autowired;

public class NetworkServiceTest {

    @Autowired
    private NetworkService service;
    
    @Test
    public void test() throws IOException {
        service.send(new Object());
        Assert.assertNull(service.receive());
    }
}
