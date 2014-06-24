package com.github.msoliter.iroh.examples.prime;

import com.github.msoliter.iroh.container.annotations.Component;

@Component
public class RemainderCalculator {

    public Long getRemainder(Long divisor, Long dividend) {
        return divisor % dividend;
    }
}
