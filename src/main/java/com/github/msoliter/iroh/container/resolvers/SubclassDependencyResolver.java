package com.github.msoliter.iroh.container.resolvers;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.github.msoliter.iroh.container.exceptions.UnexpectedImplementationCountException;
import com.github.msoliter.iroh.container.sources.Source;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class SubclassDependencyResolver implements DependencyResolver {

    private final Multimap<Class<?>, Source> sources = HashMultimap.create();
    
    @Override
    public void register(Source source) {
        synchronized (sources) {
            sources.put(source.getType(), source);
        }
    }

    @Override
    public Source resolve(Field field) 
        throws UnexpectedImplementationCountException {  
        
        Set<Source> potential = new HashSet<>();
        Class<?> target = field.getType();
        
        for (Class<?> type : sources.keySet()) {
            if (target.isAssignableFrom(type)) {
                Collection<Source> candidates = null;
                
                synchronized (sources) {
                    candidates = sources.get(type);                    
                }
                
                potential.addAll(candidates);
            }
        }

        if (potential.size() != 1) {
            throw new UnexpectedImplementationCountException(
                target, 
                potential.size());
        
        } else {
            return potential.iterator().next();
        }
    } 
}
