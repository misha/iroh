package com.misha.dedi.tests;

import org.junit.Assert;
import org.junit.Test;

import com.misha.dedi.annotations.Autowired;
import com.misha.dedi.annotations.Component;

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
