package com.github.msoliter.iroh.examples.stub;

import java.io.IOException;

import com.github.msoliter.iroh.container.annotations.Autowired;
import com.github.msoliter.iroh.container.annotations.Component;
import com.github.msoliter.iroh.examples.stub.StubExampleConfiguration.NetworkAPI;

@Component
public class NetworkServiceImpl implements NetworkService {

    @Autowired
    private NetworkAPI api;

    @Override
    public Object receive() throws IOException {
        return api.receive();
    }

    @Override
    public void send(Object message) throws IOException {
        api.send(message);
    }
}
