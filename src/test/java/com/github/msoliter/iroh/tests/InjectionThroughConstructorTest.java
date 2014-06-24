package com.github.msoliter.iroh.tests;

import org.junit.Assert;
import org.junit.Test;

import com.github.msoliter.iroh.container.annotations.Autowired;
import com.github.msoliter.iroh.container.annotations.Component;

public class InjectionThroughConstructorTest {

    @Component
    public static class Internal {
        
    }
    
    @Component
    public static class Dependency {
        
        @Autowired
        private Internal internal;
    }
    
    @Autowired
    private Dependency dependency;
    
    @Test
    public void test() {
        Assert.assertNotNull(dependency.internal);
        Assert.assertNotNull(new Dependency().internal);
    }
}
