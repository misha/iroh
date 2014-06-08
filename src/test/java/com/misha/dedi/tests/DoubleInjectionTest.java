package com.misha.dedi.tests;

import org.junit.Assert;
import org.junit.Test;

import com.misha.dedi.annotations.Autowired;

/**
 * Tests that objects are never autowired twice.
 */
public class DoubleInjectionTest {

    @Autowired
    private Object object;
    
    @Test
    public void test() {
        Object first = object;
        Object second = object;
        Assert.assertTrue(first == second);
    }
}
