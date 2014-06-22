package com.misha.dedi.tests;

import org.junit.Test;

import com.misha.dedi.annotations.Autowired;
import com.misha.dedi.annotations.Component;
import com.misha.dedi.exceptions.UnexpectedImplementationCountException;

@Component
public class TooFewSubclassesResolutionTest {
    
    public static abstract class Super {
        
    }

    @Component
    public static class Container {
        
        @Autowired(lazy = true)
        public Super instance;
    }
    
    @Autowired
    private Container container;
    
    @Test(expected = UnexpectedImplementationCountException.class)
    public void testNoImplementingClass() {
        
        @SuppressWarnings("unused")
        Super instance = container.instance;
    }
}
