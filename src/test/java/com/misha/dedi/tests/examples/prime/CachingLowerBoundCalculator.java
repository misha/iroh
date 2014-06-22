package com.misha.dedi.tests.examples.prime;

import java.util.HashMap;

import com.misha.dedi.container.annotations.Component;

@Component
public class CachingLowerBoundCalculator implements LowerBoundCalculator {

    private HashMap<Long, Long> cache = new HashMap<>();

    @Override
    public Long calculateLowerBound(Long candidate) {
        if (!cache.containsKey(candidate)) {
            cache.put(candidate, Math.round(Math.sqrt(candidate)));
        }
        
        return cache.get(candidate);
    }
}
