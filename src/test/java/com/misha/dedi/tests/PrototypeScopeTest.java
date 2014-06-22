package com.misha.dedi.tests;

import org.junit.Assert;
import org.junit.Test;

import com.misha.dedi.annotations.Autowired;
import com.misha.dedi.annotations.Component;

public class PrototypeScopeTest {
    
    @Autowired
    public PrototypeObject p0;
    
    @Autowired
    public PrototypeObject p1;
    
    @Component(scope = "prototype")
    public static class PrototypeObject {
        
    }
    
    @Test
    public void testPrototypeScope() {
        Assert.assertTrue(p0 != p1);
    }
}
