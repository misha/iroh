package com.misha.dedi.tests;

import org.junit.Assert;
import org.junit.Test;

import com.misha.dedi.annotations.Autowired;
import com.misha.dedi.annotations.Component;

/**
 * Tests that objects are never autowired twice.
 */
public class DoubleInjectionTest {

    @Component(scope = "prototype")
    public static class Container {
        
    }
    
    @Autowired
    private Container first;
    
    @Autowired
    private Container second;
    
    @Test
    public void testNoDoubleInjection() {
        Assert.assertTrue(first == first);
    }
    
    @Test
    public void testNullRefresh() {
        first = null;
        Assert.assertTrue(first != null);
    }
}
