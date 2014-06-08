package com.misha.dedi.tests;

import org.junit.Assert;
import org.junit.Test;

import com.misha.dedi.annotations.Autowired;
import com.misha.dedi.annotations.Prototype;

/**
 * Tests autowiring scoping rules.
 */
public class ScopeTest {

    @Autowired
    public SingletonObject s0;
    
    @Autowired
    public SingletonObject s1;
    
    public static class SingletonObject {
        
    }
    
    @Test
    public void testSingletonScope() {
        Assert.assertTrue(s0 == s1);
    }
    
    @Autowired
    public PrototypeObject p0;
    
    @Autowired
    public PrototypeObject p1;
    
    @Prototype
    public static class PrototypeObject {
        
    }
    
    @Test
    public void testPrototypeScope() {
        Assert.assertTrue(p0 != p1);
    }
}
