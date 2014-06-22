package com.misha.dedi.managers;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.reflections.Reflections;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.misha.dedi.annotations.Component;
import com.misha.dedi.exceptions.DediException;
import com.misha.dedi.exceptions.NonConcreteComponentClassException;
import com.misha.dedi.sources.MethodSource;
import com.misha.dedi.sources.Source;
import com.misha.dedi.sources.TypeSource;

public class SourceManager {
    
    private final DependencyResolverManager resolver;
    
    private final Multimap<Class<?>, Source> sources = HashMultimap.create();
    
    private final Reflections reflections = new Reflections("");

    public SourceManager() throws NonConcreteComponentClassException {
        for (Class<?> type : reflections.getTypesAnnotatedWith(Component.class)) {
            Source typeSource = new TypeSource(type);
            sources.put(typeSource.getType(), typeSource);

            for (Method method : type.getMethods()) {
                Component annotation = method.getAnnotation(Component.class);
                
                if (annotation != null) {
                    Source methodSource = new MethodSource(typeSource, method);
                    sources.put(methodSource.getType(), methodSource);
                }
            }
        }
        
        resolver = new DependencyResolverManager(sources);
    }
    
    public Class<?> getImplementingType(Field field) throws DediException {
        return resolver.resolve(field).getType();
    }
    
    public Object getInstance(Field field) throws DediException {
        return resolver.resolve(field).getInstance();
    }
}
