package com.misha.dedi.tests;

import org.junit.Test;

import com.misha.dedi.annotations.Autowired;
import com.misha.dedi.annotations.Component;
import com.misha.dedi.exceptions.NoZeroArgumentConstructorException;

/**
 * Tests that the framework throws the correct exception when a default 
 * constructor is not available.
 */
@Component
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
