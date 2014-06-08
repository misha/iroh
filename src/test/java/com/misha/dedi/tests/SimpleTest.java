package com.misha.dedi.tests;

import org.junit.Assert;
import org.junit.Test;

import com.misha.dedi.annotations.Autowired;

/**
 * Tests that a simple two-level autowiring works.
 */
public class SimpleTest {
    
    @Autowired
    private Dependency dependency;
    
    @Test
    public void test() {
        Assert.assertNotNull(dependency);
        Assert.assertNotNull(dependency.internal);
    }
    
    public static class Dependency {
        
        @Autowired
        public InternalDependency internal;
    }
    
    public static class InternalDependency {
        
    }
}
