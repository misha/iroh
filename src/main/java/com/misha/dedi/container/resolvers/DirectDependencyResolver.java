package com.misha.dedi.container.resolvers;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;

import com.google.common.collect.Multimap;
import com.misha.dedi.container.exceptions.DediException;
import com.misha.dedi.container.sources.Source;

public class DirectDependencyResolver implements DependencyResolver {

    private Multimap<Class<?>, Source> sources;
    
    @Override
    public void initialize(Multimap<Class<?>, Source> sources) {
        this.sources = sources;
    }

    @Override
    public Source resolve(Field field) throws DediException {
        Collection<Source> potential = sources.get(field.getType());
        Iterator<Source> iterator = potential.iterator();
        
        while (iterator.hasNext()) {
            Source candidate = iterator.next();
            
            if (!candidate.isConcrete()) {
                iterator.remove();
            }
        }
        
        if (potential.size() == 1) {
            Source source = potential.iterator().next();
            
            if (source.isConcrete()) {
                return source;
            }
        }
        
        return null;
    }
}
