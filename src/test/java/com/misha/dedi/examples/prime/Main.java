package com.misha.dedi.examples.prime;

import java.util.Random;

import org.junit.Test;

import com.misha.dedi.annotations.Autowired;

public class Main {    
    
    @Autowired
    private PrimalityCalculator calculator;
    
    @Test
    public void test() {
        main(new String[] {
            "2",
            "4",
            "13",
            "3294834",
            "32948377"
        });
    }
    
    public void main(String[] arguments) {        
        if (arguments.length > 0) {
            for (String argument : arguments) {
                try {
                    Long candidate = Long.parseLong(argument);
                    System.out.println(String.format(
                        "%s is %s.",
                        argument,
                        calculator.isPrime(candidate) ? "prime" : "not prime"));
            
                } catch (NumberFormatException e) {
                    System.out.println(String.format(
                        "%s is not a valid number.",
                        argument));
                }
            }
        
        } else {
            Long random = new Random().nextLong() % (2 << 16);
            System.out.println(String.format(
                "%s is %s.",
                random.toString(),
                calculator.isPrime(random) ? "prime" : "not prime"));
        }
    }
}
