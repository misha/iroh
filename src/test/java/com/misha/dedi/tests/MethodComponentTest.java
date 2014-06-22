package com.misha.dedi.tests;

import org.junit.Assert;
import org.junit.Test;

import com.misha.dedi.annotations.Autowired;
import com.misha.dedi.annotations.Component;

/**
 * Tests that methods annotated with @Component correctly get called.
 */
@Component
public class MethodComponentTest {

    static int count = 0;
    
    @Component
    public static class Dependency {
     
        @Component
        public Object buildObjectDependency() {
            count += 1;
            return new Object();
        }
    }
    
    @Component
    public static class Container {
        
        @Autowired(lazy = true)
        public Object dependency;
    }
    
    @Autowired
    public Container container;
    
    @Test
    public void testLazyMethodInjection() {
        Assert.assertNotNull(container);
        Assert.assertTrue(count == 0);
        Assert.assertNotNull(container.dependency);
        Assert.assertTrue(count == 1);
    }
}
