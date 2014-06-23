package com.misha.dedi.tests;

import org.junit.Assert;
import org.junit.Test;

import com.misha.dedi.container.annotations.Autowired;
import com.misha.dedi.container.annotations.Component;

public class ResolvableTriangularDependencyTest {

    public static abstract class Root {
        
        public abstract void validate();
    }
    
    @Component(qualifier = "ResolvableTriangularDependencyTest.A")
    public static class ImplA extends Root {
        
        public void validate() {
            
        }
    }
    
    @Component(qualifier = "ResolvableTriangularDependencyTest.B")
    public static class ImplB extends Root {
        
        @Autowired(qualifier = "ResolvableTriangularDependencyTest.A")
        private Root a;
        
        public void validate() {
            Assert.assertTrue(a instanceof ImplA);
        }
    }
    
    @Component(qualifier = "ResolvableTriangularDependencyTest.C")
    public static class ImplC extends Root {
        
        @Autowired(qualifier = "ResolvableTriangularDependencyTest.A")
        private Root a;
        
        @Autowired(qualifier = "ResolvableTriangularDependencyTest.B")
        private Root b;
        
        public void validate() {
            Assert.assertTrue(a instanceof ImplA);
            Assert.assertTrue(b instanceof ImplB);
        }
    }
    
    @Autowired(qualifier = "ResolvableTriangularDependencyTest.C")
    private Root c;
    
    @Test
    public void test() {
        Assert.assertTrue(c instanceof ImplC);
        c.validate();
    }
}
