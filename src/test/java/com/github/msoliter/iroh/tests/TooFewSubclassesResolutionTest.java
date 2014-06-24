package com.github.msoliter.iroh.tests;

import org.junit.Test;

import com.github.msoliter.iroh.container.annotations.Autowired;
import com.github.msoliter.iroh.container.exceptions.UnexpectedImplementationCountException;

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
