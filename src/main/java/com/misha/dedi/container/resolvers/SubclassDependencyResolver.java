package com.misha.dedi.container.resolvers;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.Multimap;
import com.misha.dedi.container.exceptions.DediException;
import com.misha.dedi.container.exceptions.UnexpectedImplementationCountException;
import com.misha.dedi.container.sources.Source;

public class SubclassDependencyResolver implements DependencyResolver {

    private Multimap<Class<?>, Source> sources;
    
    @Override
    public void initialize(Multimap<Class<?>, Source> sources) {
        this.sources = sources;
    }

    @Override
    public Source resolve(Field field) throws DediException {       
        Set<Source> potential = new HashSet<>();
        Class<?> target = field.getType();
        
        for (Class<?> type : sources.keySet()) {
            if (target.isAssignableFrom(type)) {
                for (Source source : sources.get(type)) {
                    if (source.isConcrete()) {
                        potential.add(source);
                    }
                }
            }
        }

        if (potential.size() != 1) {
            throw new UnexpectedImplementationCountException(target, potential.size());
        
        } else {
            return potential.iterator().next();
        }
    } 
}
