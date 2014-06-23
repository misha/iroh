package com.misha.dedi.tests;

import org.junit.Test;

import com.misha.dedi.container.annotations.Autowired;
import com.misha.dedi.container.annotations.Component;

public class TriangularDependencyTest {

    @Component
    public static class A {
        
    }
    
    @Component
    public static class B {
        
        @Autowired
        private A a;
    }
    
    @Component
    public static class C {
        
        @Autowired
        private A a;
        
        @Autowired
        private B b;
    }
    
    @Test
    public void test() { 
        
    }
}
