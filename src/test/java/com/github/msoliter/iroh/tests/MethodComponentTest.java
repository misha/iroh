package com.github.msoliter.iroh.tests;

import org.junit.Assert;
import org.junit.Test;

import com.github.msoliter.iroh.container.annotations.Autowired;
import com.github.msoliter.iroh.container.annotations.Component;

/**
 * Tests that methods annotated with @Component correctly get called.
 */
public class MethodComponentTest {

    private static int count = 0;
    
    public static class Dependency {
        
    }
    
    @Component
    public static class DependencyFactory {
     
        @Component
        public Dependency object() {
            count += 1;
            return new Dependency();
        }
    }
    
    public static class Container {
        
        @Autowired
        public Dependency dependency;
    }
    
    @Test
    public void testLazyMethodInjection() {
        Assert.assertTrue(count == 0);
        new Container();
        Assert.assertTrue(count == 1);
    }
}
