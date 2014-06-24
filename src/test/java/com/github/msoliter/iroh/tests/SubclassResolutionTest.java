package com.github.msoliter.iroh.tests;

import org.junit.Assert;
import org.junit.Test;

import com.github.msoliter.iroh.container.annotations.Autowired;
import com.github.msoliter.iroh.container.annotations.Component;

/**
 * Tests that subclasses are properly injected for their super classes.
 */
public class SubclassResolutionTest {

    public static abstract class Super {

    }
    
    @Component
    public static class Sub extends Super {

    }

    @Component
    public static class Container {
        
        @Autowired
        public Super instance;
    }
    
    @Test
    public void testSubclassResolution() {
        Container container = new Container();
        Assert.assertNotNull(container);
        Assert.assertNotNull(container.instance);
        Assert.assertTrue(container.instance instanceof Sub);
    }
}
