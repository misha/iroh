package com.github.msoliter.iroh.examples.network;

import com.github.msoliter.iroh.container.annotations.Component;
import com.github.msoliter.iroh.examples.network.NetworkServiceConfiguration.NetworkAPI;

@Component
public class NetworkServiceTestConfiguration {

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
