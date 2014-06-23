package com.misha.dedi.tests;

import org.junit.Assert;
import org.junit.Test;

import com.misha.dedi.container.annotations.Autowired;
import com.misha.dedi.container.annotations.Component;

/**
 * Attempts to crack open any concurrency problems inside the framework.
 */
public class ConcurrencyTest {

    @Component(scope = "prototype")
    public static class A {
        
    }
    
    @Component(scope = "prototype")
    public static class B {
        
        @Autowired
        public A a;
    }
    
    @Component(scope = "prototype")
    public static class C {
        
        @Autowired
        public A a;
        
        @Autowired
        public B b;
    }
    
    @Component(scope = "prototype")
    public static class D {
        
        @Autowired
        public A a;
        
        @Autowired
        public B b;
        
        @Autowired
        public C c;
    }
    
    @Test
    public void testBombardment() {
        int threads = 10;
        Thread previous = null;
        
        for (int i = 0; i < threads; i += 1) {
            previous = new TestThread(previous);
            previous.start();
        }
    }
    
    public static class TestThread extends Thread {
        
        private Thread previous = null;
        
        private int iterations = 100000;
        
        public TestThread(Thread previous) {
            this.previous = previous;
        }
        
        @Override
        public void run() {
            work();
            
            if (previous != null) {
                try {
                    previous.join();
                    
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }
        
        private void work() {
            while (iterations > 0) {
                D object = new D();
                Assert.assertNotNull(object.c.b.a);
                iterations -= 1;
            }
        }
    }
}
