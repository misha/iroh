package com.github.msoliter.iroh.tests;

import org.junit.Test;

import com.github.msoliter.iroh.container.annotations.Autowired;
import com.github.msoliter.iroh.container.annotations.Component;
import com.github.msoliter.iroh.container.exceptions.NoZeroArgumentConstructorException;

/**
 * Tests that the framework throws the correct exception when a default 
 * constructor is not available.
 */
public class BlockedConstructorTest {
    
    @Autowired(lazy = true)
    public Dependency dependency;

    @Component
    public static class Dependency {
        
        public Dependency(String argument) {
            System.out.println("This obstructs the zero argument constructor.");
        }
    }
    
    @Test(expected = NoZeroArgumentConstructorException.class)
    public void test() {
        
        @SuppressWarnings("unused")
        Dependency dependency = this.dependency;
    }
}
