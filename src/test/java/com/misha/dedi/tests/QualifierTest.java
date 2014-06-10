package com.misha.dedi.tests;

import org.junit.Assert;
import org.junit.Test;

import com.misha.dedi.annotations.Autowired;
import com.misha.dedi.annotations.Component;

/**
 * Alright, getting pretty complicated. Tests that the qualifier annotation
 * on types allows autowired annotations to specify which implementation should
 * be injected.
 */
public class QualifierTest {

    @Autowired(qualifier = "test")
    public Qualified qualified;
    
    @Component(qualifier = "test")
    public static class Qualified {
        
    }
    
    /**
     * The degenerate case where a single class is qualified with no other
     * alternatives or super classes.
     */
    @Test
    public void testSimpleQualifier() {
        Assert.assertNotNull(qualified);
    }
    
    @Autowired(qualifier = "A")
    public BaseClass a;
    
    @Autowired(qualifier = "B")
    public BaseClass b;
    
    public static class BaseClass {
        
    }
    
    @Component(qualifier = "A")
    public static class AClass extends BaseClass {
        
    }
    
    @Component(qualifier = "B")
    public static class BClass extends BaseClass {
        
    }
    
    @Test
    public void testQualifier() {
        Assert.assertTrue(a instanceof AClass);
        Assert.assertTrue(b instanceof BClass);
    }
}
