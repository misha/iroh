package com.misha.dedi.tests;

import org.junit.Assert;
import org.junit.Test;

import com.misha.dedi.annotations.Autowired;
import com.misha.dedi.annotations.Component;

/**
 * Tests that objects are never autowired twice.
 */
@Component
public class DoubleInjectionTest {

    public DoubleInjectionTest() {
        
    }
    
    @Component(scope = "prototype")
    public static class Container {
        
    }
    
    @Autowired
    private Container first;
    
    @Test
    public void testNoDoubleInjection() {
        Assert.assertTrue(first == first);
        Assert.assertNotNull(first);
    }
    
    @Test
    public void testNullRefresh() {
        first = null;
        Assert.assertNull(first);
    }
}
