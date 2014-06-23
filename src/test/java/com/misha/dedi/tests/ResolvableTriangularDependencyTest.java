package com.misha.dedi.tests;

import org.junit.Test;

import com.misha.dedi.container.annotations.Autowired;
import com.misha.dedi.container.annotations.Component;

public class ResolvableTriangularDependencyTest {

    public static abstract class Root {
        
    }
    
    @Component(qualifier = "ResolvableTriangularDependencyTest.A")
    public static class ImplA extends Root {
        
    }
    
    @Component(qualifier = "ResolvableTriangularDependencyTest.B")
    public static class ImplB extends Root {
        
        @Autowired(qualifier = "ResolvableTriangularDependencyTest.A")
        private Root a;
    }
    
    @Component(qualifier = "ResolvableTriangularDependencyTest.C")
    public static class ImplC extends Root {
        
        @Autowired(qualifier = "ResolvableTriangularDependencyTest.A")
        private Root a;
        
        @Autowired(qualifier = "ResolvableTriangularDependencyTest.B")
        private Root b;
    }
    
    @Test
    public void test() {
        
    }
}
