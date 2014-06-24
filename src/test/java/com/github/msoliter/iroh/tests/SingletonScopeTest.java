package com.github.msoliter.iroh.tests;

import org.junit.Assert;
import org.junit.Test;

import com.github.msoliter.iroh.container.annotations.Autowired;
import com.github.msoliter.iroh.container.annotations.Component;

/**
 * Tests autowiring scoping rules.
 */
public class SingletonScopeTest {

    @Autowired
    public SingletonObject s0;
    
    @Autowired
    public SingletonObject s1;
    
    @Component
    public static class SingletonObject {
        
    }
    
    @Test
    public void testSingletonScope() {
        Assert.assertTrue(s0 == s1);
    }
}
