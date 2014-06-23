package com.misha.dedi.container.resolvers;

import java.lang.reflect.Field;

import com.misha.dedi.container.exceptions.DediException;
import com.misha.dedi.container.sources.Source;

public interface DependencyResolver {
    
    public void register(Source source) throws DediException;

    public Source resolve(Field field) throws DediException;
}
