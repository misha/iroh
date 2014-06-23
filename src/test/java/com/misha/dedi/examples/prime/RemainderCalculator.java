package com.misha.dedi.examples.prime;

import com.misha.dedi.container.annotations.Component;

@Component
public class RemainderCalculator {

    public Long getRemainder(Long divisor, Long dividend) {
        return divisor % dividend;
    }
}
