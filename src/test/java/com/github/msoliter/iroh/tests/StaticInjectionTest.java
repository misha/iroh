package com.github.msoliter.iroh.tests;

import org.junit.Assert;
import org.junit.Test;

import com.github.msoliter.iroh.container.annotations.Autowired;
import com.github.msoliter.iroh.container.annotations.Component;

public class StaticInjectionTest {

    @Component
    public static class Dependency {
        
        public Dependency() {
            
        }
    }
    
    @Autowired
    private static Dependency DEPENDENCY;
    
    @Test
    public void testNotInjected(){
        Assert.assertNotNull(DEPENDENCY);
    }
}
