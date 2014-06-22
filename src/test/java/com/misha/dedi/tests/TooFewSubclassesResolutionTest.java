package com.misha.dedi.tests;

import org.junit.Test;

import com.misha.dedi.container.annotations.Autowired;
import com.misha.dedi.container.exceptions.UnexpectedImplementationCountException;

public class TooFewSubclassesResolutionTest {
    
    public static abstract class Super {
        
    }

    public static class Container {
        
        @Autowired
        public Super instance;
    }
    
    @Test(expected = UnexpectedImplementationCountException.class)
    public void testNoImplementingClass() {
        new Container();
    }
}
