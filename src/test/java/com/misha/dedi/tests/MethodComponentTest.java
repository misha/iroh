package com.misha.dedi.tests;

import org.junit.Assert;
import org.junit.Test;

import com.misha.dedi.annotations.Autowired;
import com.misha.dedi.annotations.Component;

/**
 * Tests that methods annotated with @Component correctly get called.
 */
public class MethodComponentTest {

    static int count = 0;
    
    @Component
    public static class DependencyFactory {
     
        @Component
        public Object object() {
            count += 1;
            return new Object();
        }
    }
    
    public static class Container {
        
        @Autowired
        public Object dependency;
    }
    
    @Test
    public void testLazyMethodInjection() {
        Assert.assertTrue(count == 0);
        new Container();
        Assert.assertTrue(count == 1);
    }
}
