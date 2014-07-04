package com.github.msoliter.iroh.examples.stub;

import java.io.IOException;

import com.github.msoliter.iroh.container.annotations.Component;

@Component
public class StubExampleConfiguration {

    public static class NetworkAPI {
        
        public void send(Object object) throws IOException {
            throw new IOException();
        }
        
        public Object receive() throws IOException {
            throw new IOException();
        }
    }
    
    @Component
    public NetworkAPI networkApi() {
        return new NetworkAPI();
    }
}
