package com.github.msoliter.iroh.tests;

import org.junit.Assert;
import org.junit.Test;

import com.github.msoliter.iroh.container.annotations.Autowired;
import com.github.msoliter.iroh.container.annotations.Component;

/**
 * Tests that a simple two-level autowiring works.
 */
public class SimpleTest {
    
    @Component
    public static class InternalDependency {
        
    }
    
    public static class Dependency {
        
        @Autowired
        public InternalDependency internal;
    }
    
    @Test
    public void test() {
        Assert.assertNotNull(new Dependency().internal);
    }
}
