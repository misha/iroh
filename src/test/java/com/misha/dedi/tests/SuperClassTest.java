package com.misha.dedi.tests;

import org.junit.Assert;
import org.junit.Test;

import com.misha.dedi.annotations.Autowired;
import com.misha.dedi.exceptions.UnexpectedImplementationCountException;

/**
 * Tests that subclasses are properly injected for their super classes.
 */
public class SuperClassTest {

    public static abstract class Super {
        
    }
    
    public static class Sub extends Super {
        
    }

    public static class Container {
        
        @Autowired
        public Super instance;
    }
    
    @Autowired
    private Container container;
    
    @Test
    public void testSuperClassResolution() {
        Assert.assertTrue(container.instance instanceof Super);
    }
    
    public static abstract class Super2 {
        
    }

    public static class Container2 {
        
        @Autowired
        public Super2 instance;
    }
    
    @Autowired
    private Container2 container2;
    
    @Test(expected = UnexpectedImplementationCountException.class)
    public void testNoImplementingClass() {
        
        @SuppressWarnings("unused")
        Super2 instance = container2.instance;
    }
    
    public static abstract class Super3 {
        
    }
    
    public static class Sub3A extends Super3 {
        
    }
    
    public static class Sub3B extends Super3 {
        
    }
    
    public static class Container3 {
        
        @Autowired
        public Super3 instance;
    }
    
    @Autowired
    public Container3 container3;
    
    @Test(expected = UnexpectedImplementationCountException.class)
    public void testTooManyImplementingClasses() {
        
        @SuppressWarnings("unused")
        Super3 instance = container3.instance;
    }
}
