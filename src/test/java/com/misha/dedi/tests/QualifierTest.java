package com.misha.dedi.tests;

import org.junit.Assert;
import org.junit.Test;

import com.misha.dedi.container.annotations.Autowired;
import com.misha.dedi.container.annotations.Component;

/**
 * Alright, getting pretty complicated. Tests that the qualifier annotation
 * on types allows autowired annotations to specify which implementation should
 * be injected.
 */
public class QualifierTest {
    
    @Autowired(qualifier = "QualifierTest.A")
    public BaseClass a;
    
    @Autowired(qualifier = "QualifierTest.B")
    public BaseClass b;
    
    public static class BaseClass {
        
    }
    
    @Component(qualifier = "QualifierTest.A")
    public static class AClass extends BaseClass {
        
    }
    
    @Component(qualifier = "QualifierTest.B")
    public static class BClass extends BaseClass {
        
    }
    
    @Test
    public void testQualifier() {
        Assert.assertTrue(a instanceof AClass);
        Assert.assertTrue(b instanceof BClass);
    }
}
