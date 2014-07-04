package com.github.msoliter.iroh.examples.network;

import java.io.IOException;

import com.github.msoliter.iroh.container.annotations.Component;

@Component
public class NetworkServiceConfiguration {

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
