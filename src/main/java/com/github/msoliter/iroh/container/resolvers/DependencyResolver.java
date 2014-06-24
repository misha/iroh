package com.github.msoliter.iroh.container.resolvers;

import java.lang.reflect.Field;

import com.github.msoliter.iroh.container.sources.Source;

public interface DependencyResolver {
    
    public void register(Source source);

    public Source resolve(Field field);
}
