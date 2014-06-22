package com.misha.dedi.examples.prime;

import com.misha.dedi.annotations.Component;

@Component
public class RemainderCalculator {

    public Long getRemainder(Long divisor, Long dividend) {
        return divisor % dividend;
    }
}
