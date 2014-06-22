package com.misha.dedi.tests;

import org.junit.Test;

import com.misha.dedi.container.annotations.Autowired;
import com.misha.dedi.container.annotations.Component;
import com.misha.dedi.container.exceptions.UnexpectedImplementationCountException;

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
    
    @Test(expected = UnexpectedImplementationCountException.class)
    public void testTooManyImplementingClasses() {
        new Container();
    }
}
