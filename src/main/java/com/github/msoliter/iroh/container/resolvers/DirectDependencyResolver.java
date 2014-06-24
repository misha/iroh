package com.github.msoliter.iroh.container.resolvers;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.github.msoliter.iroh.container.sources.Source;

public class DirectDependencyResolver implements DependencyResolver {

    private final Map<Class<?>, Source> sources = new ConcurrentHashMap<>();
    
    @Override
    public void register(Source source) {
        sources.put(source.getType(), source);
    }

    @Override
    public Source resolve(Field field) {
        return sources.get(field.getType());
    }
}
