package com.misha.dedi.resolvers;

import java.lang.reflect.Field;

import com.google.common.collect.Multimap;
import com.misha.dedi.exceptions.DediException;
import com.misha.dedi.sources.Source;

public interface DependencyResolver {
    
    public void initialize(Multimap<Class<?>, Source> sources);

    public Source resolve(Field field) throws DediException;
}
