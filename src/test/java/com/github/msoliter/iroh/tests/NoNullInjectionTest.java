package com.github.msoliter.iroh.tests;

import org.junit.Assert;
import org.junit.Test;

import com.github.msoliter.iroh.container.annotations.Autowired;
import com.github.msoliter.iroh.container.annotations.Component;
import com.github.msoliter.iroh.container.annotations.Scope;

public class NoNullInjectionTest {
    
    @Component(scope = Scope.PROTOTYPE)
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
