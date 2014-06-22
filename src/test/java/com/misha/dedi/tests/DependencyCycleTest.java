package com.misha.dedi.tests;

import com.misha.dedi.annotations.Autowired;
import com.misha.dedi.annotations.Component;

public class DependencyCycleTest {

    @Component
    public static class A {
     
        @Autowired
        private B b;
    }
    
    @Component
    public static class B {
        
        @Autowired
        private A a;
    }
    
    // @Test(expected = DependencyCycleException.class)
    public void test() {
        new A();
    }
}