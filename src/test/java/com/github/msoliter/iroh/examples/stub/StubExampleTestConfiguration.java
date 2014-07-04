package com.github.msoliter.iroh.examples.stub;

import com.github.msoliter.iroh.container.annotations.Component;
import com.github.msoliter.iroh.examples.stub.StubExampleConfiguration.NetworkAPI;

@Component
public class StubExampleTestConfiguration {

    @Component(override = true)
    public NetworkAPI stubNetworkApi() {
        return new NetworkAPI() {
            public void send(Object object) {

            }
            
            public Object receive() {
                return null;
            }
        };
    }
}
