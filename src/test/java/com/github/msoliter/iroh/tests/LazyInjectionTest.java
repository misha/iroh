package com.github.msoliter.iroh.tests;

import org.junit.Assert;
import org.junit.Test;

import com.github.msoliter.iroh.container.annotations.Autowired;
import com.github.msoliter.iroh.container.annotations.Component;

public class LazyInjectionTest {

    private static int count = 0;
    
    @Component
    public static class Dependency {
        
        public Dependency() {
            count += 1;
        }
    }
    
    @Autowired(lazy = true)
    private Dependency dependency;
    
    @Test
    public void testLazyInjection() {
        Assert.assertTrue(count == 0);
        Assert.assertNotNull(dependency);
        Assert.assertTrue(count == 1);
    }
}
