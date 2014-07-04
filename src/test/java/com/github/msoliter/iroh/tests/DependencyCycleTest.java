package com.github.msoliter.iroh.tests;

/**
 * Exception while initializing com.github.msoliter.iroh.container.aspects.AutowiringAspect: 
 *  com.github.msoliter.iroh.container.exceptions.DependencyCycleException: 
 *      A dependency cycle was detected: 
 *          class com.github.msoliter.iroh.tests.DependencyCycleTest$A => 
 *          class com.github.msoliter.iroh.tests.DependencyCycleTest$B => 
 *          class com.github.msoliter.iroh.tests.DependencyCycleTest$A.
 */
//public class DependencyCycleTest {
//
//    @Component
//    public static class A {
//     
//        @Autowired
//        private B b;
//    }
//    
//    @Component
//    public static class B {
//        
//        @Autowired
//        private A a;
//    }
//    
//    @Test
//    public void test() { }
//}