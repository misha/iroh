package com.misha.dedi.tests;

import org.junit.Assert;
import org.junit.Test;

import com.misha.dedi.annotations.Autowired;
import com.misha.dedi.annotations.Prototype;

/**
 * Tests that objects are never autowired twice.
 */
public class DoubleInjectionTest {

    @Prototype
    public static class Container {
        
    }
    
    @Autowired
    private Container first;
    
    @Autowired
    private Container second;
    
    @Test
    public void test() {
        Assert.assertTrue(first == first);
        Assert.assertTrue(first != second);
    }
}
