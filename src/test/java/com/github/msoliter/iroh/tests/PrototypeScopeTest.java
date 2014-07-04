package com.github.msoliter.iroh.tests;

import org.junit.Assert;
import org.junit.Test;

import com.github.msoliter.iroh.container.annotations.Autowired;
import com.github.msoliter.iroh.container.annotations.Component;
import com.github.msoliter.iroh.container.annotations.Scope;

public class PrototypeScopeTest {
    
    @Autowired
    public PrototypeObject p0;
    
    @Autowired
    public PrototypeObject p1;
    
    @Component(scope = Scope.PROTOTYPE)
    public static class PrototypeObject {
        
    }
    
    @Test
    public void testPrototypeScope() {
        Assert.assertTrue(p0 != p1);
    }
}
