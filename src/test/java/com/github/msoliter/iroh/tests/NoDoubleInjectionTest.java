package com.github.msoliter.iroh.tests;

import org.junit.Assert;
import org.junit.Test;

import com.github.msoliter.iroh.container.annotations.Autowired;
import com.github.msoliter.iroh.container.annotations.Component;

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
