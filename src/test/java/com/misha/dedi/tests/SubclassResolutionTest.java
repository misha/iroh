package com.misha.dedi.tests;

import org.junit.Assert;
import org.junit.Test;

import com.misha.dedi.container.annotations.Autowired;
import com.misha.dedi.container.annotations.Component;

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
