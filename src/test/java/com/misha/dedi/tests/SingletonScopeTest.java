package com.misha.dedi.tests;

import org.junit.Assert;
import org.junit.Test;

import com.misha.dedi.annotations.Autowired;
import com.misha.dedi.annotations.Component;

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
