package com.github.msoliter.iroh.tests;

import org.junit.Assert;
import org.junit.Test;

import com.github.msoliter.iroh.container.annotations.Autowired;
import com.github.msoliter.iroh.container.annotations.Component;

public class DegenerateQualifierTest {

    @Autowired(qualifier = "DegenerateQualifierTest.test")
    public Qualified qualified;
    
    @Component(qualifier = "DegenerateQualifierTest.test")
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
