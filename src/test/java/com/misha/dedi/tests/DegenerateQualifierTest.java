package com.misha.dedi.tests;

import org.junit.Assert;
import org.junit.Test;

import com.misha.dedi.annotations.Autowired;
import com.misha.dedi.annotations.Component;

@Component
public class DegenerateQualifierTest {

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
}
