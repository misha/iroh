package com.misha.sid.tests.simple;

import org.junit.Assert;
import org.junit.Test;

import com.misha.sid.annotations.Autowired;
import com.misha.sid.example.Dependency;

public class SimpleTest {
    
    @Autowired
    private Dependency dependency;
    
    @Test
    public void testSimple() {
        Assert.assertNotNull(dependency);
        Assert.assertNotNull(dependency.getDependency());
    }
}
