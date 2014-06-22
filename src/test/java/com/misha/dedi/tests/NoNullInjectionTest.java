package com.misha.dedi.tests;

import org.junit.Assert;
import org.junit.Test;

import com.misha.dedi.annotations.Autowired;
import com.misha.dedi.annotations.Component;

@Component
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
