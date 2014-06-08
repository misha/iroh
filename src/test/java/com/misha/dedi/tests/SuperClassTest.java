package com.misha.dedi.tests;

import org.junit.Assert;
import org.junit.Test;

import com.misha.dedi.annotations.Autowired;

/**
 * Tests that subclasses are properly injected for their super classes.
 */
public class SuperClassTest {

    public static class Container {
        
        @Autowired
        public Super instance;
    }
    
    public static abstract class Super {
        
    }
    
    public static class Sub extends Super {
        
    }
    
    @Autowired
    private Container container;
    
    @Test
    public void testSuperClassResolution() {
        Assert.assertTrue(container.instance instanceof Super);
    }
}
