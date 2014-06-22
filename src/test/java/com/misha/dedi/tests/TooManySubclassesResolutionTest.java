package com.misha.dedi.tests;

import org.junit.Test;

import com.misha.dedi.annotations.Autowired;
import com.misha.dedi.annotations.Component;
import com.misha.dedi.exceptions.UnexpectedImplementationCountException;

@Component
public class TooManySubclassesResolutionTest {
    
    public static abstract class Super {
        
    }
    
    @Component
    public static class SubA extends Super {
        
    }
    
    @Component
    public static class SubB extends Super {
        
    }
    
    @Component
    public static class Container {
        
        @Autowired(lazy = true)
        public Super instance;
    }
    
    @Autowired
    public Container container;
    
    @Test(expected = UnexpectedImplementationCountException.class)
    public void testTooManyImplementingClasses() {
        
        @SuppressWarnings("unused")
        Super instance = container.instance;
    }
}
