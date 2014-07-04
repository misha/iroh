package com.github.msoliter.iroh.examples.stub;

import java.io.IOException;

public interface NetworkService {

    public Object receive() throws IOException;
    
    public void send(Object message) throws IOException;
}
