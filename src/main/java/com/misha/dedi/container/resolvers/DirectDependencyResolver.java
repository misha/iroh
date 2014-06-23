package com.misha.dedi.container.resolvers;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.misha.dedi.container.exceptions.DediException;
import com.misha.dedi.container.sources.Source;

public class DirectDependencyResolver implements DependencyResolver {

    private final Map<Class<?>, Source> sources = new ConcurrentHashMap<>();
    
    @Override
    public void register(Source source) {
        sources.put(source.getType(), source);
    }

    @Override
    public Source resolve(Field field) throws DediException {
        return sources.get(field.getType());
    }
}
