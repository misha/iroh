package com.misha.dedi.tests;

import org.junit.Assert;
import org.junit.Test;

import com.misha.dedi.container.annotations.Autowired;
import com.misha.dedi.container.annotations.Component;

public class NoNullInjectionTest {
    
    @Component(scope = "prototype")
    public static class Dependency {
        
    }
    
    @Autowired
    private Dependency dependency;
    
    @Test
    public void testNullRefresh() {
        dependency = null;
        Assert.assertNull(dependency);
    }
}
