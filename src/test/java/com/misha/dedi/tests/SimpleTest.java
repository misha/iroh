package com.misha.dedi.tests;

import org.junit.Assert;
import org.junit.Test;

import com.misha.dedi.annotations.Autowired;
import com.misha.dedi.annotations.Component;

/**
 * Tests that a simple two-level autowiring works.
 */
public class SimpleTest {
    
    @Autowired
    private Dependency dependency;
    
    @Component
    public static class Dependency {
        
        @Autowired
        public InternalDependency internal;
    }
    
    @Component
    public static class InternalDependency {
        
    }
    
    @Test
    public void test() {
        Assert.assertNotNull(dependency);
        Assert.assertNotNull(dependency.internal);
    }
}
