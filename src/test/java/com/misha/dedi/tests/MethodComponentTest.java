package com.misha.dedi.tests;

import org.junit.Assert;
import org.junit.Test;

import com.misha.dedi.annotations.Autowired;
import com.misha.dedi.annotations.Component;

/**
 * Tests that methods annotated with @Component correctly get called.
 */
public class MethodComponentTest {

    @Component
    public static class Dependency {
     
        @Component
        public Object buildObjectDependency() {
            return new Object();
        }
    }
    
    @Component
    public static class Container {
        
        @Autowired(lazy = true)
        public Object dependency;
    }
    
    @Autowired
    public Container container;
    
    @Test
    public void test() {
        Assert.assertNotNull(container.dependency);
    }
}
