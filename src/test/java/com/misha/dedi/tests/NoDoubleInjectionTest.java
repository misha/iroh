package com.misha.dedi.tests;

import org.junit.Assert;
import org.junit.Test;

import com.misha.dedi.annotations.Autowired;
import com.misha.dedi.annotations.Component;

/**
 * Tests that objects are never autowired twice.
 */
public class NoDoubleInjectionTest {

    @Component(scope = "prototype")
    public static class Dependency {
        
    }
    
    @Autowired
    private Dependency dependency;
    
    @Test
    public void testNoDoubleInjection() {
        Assert.assertTrue(dependency == dependency);
        Assert.assertNotNull(dependency);
    }
}
