package com.misha.dedi.tests;

import org.junit.Assert;
import org.junit.Test;

import com.misha.dedi.annotations.Autowired;
import com.misha.dedi.annotations.Component;

/**
 * Tests that subclasses are properly injected for their super classes.
 */
@Component
public class SubclassResolutionTest {

    public static abstract class Super {
        
    }
    
    @Component
    public static class Sub extends Super {
        
    }

    @Component
    public static class Container {
        
        @Autowired(lazy = true)
        public Super instance;
    }
    
    @Autowired
    private Container container;
    
    @Test
    public void testSubclassResolution() {
        Assert.assertTrue(container.instance instanceof Sub);
    }
}
