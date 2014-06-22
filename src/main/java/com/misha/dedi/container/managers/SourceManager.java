package com.misha.dedi.container.managers;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.reflections.Reflections;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.misha.dedi.container.annotations.Autowired;
import com.misha.dedi.container.annotations.Component;
import com.misha.dedi.container.exceptions.DediException;
import com.misha.dedi.container.exceptions.DependencyCycleException;
import com.misha.dedi.container.sources.MethodSource;
import com.misha.dedi.container.sources.Source;
import com.misha.dedi.container.sources.TypeSource;

public class SourceManager {
    
    private final DependencyResolverManager resolver;
    
    private final Multimap<Class<?>, Source> sources = HashMultimap.create();
    
    private final Reflections reflections = new Reflections("");

    public SourceManager() throws DediException {
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
        
        checkForCycles();
        resolver = new DependencyResolverManager(sources);
    }
    
    public Class<?> getImplementingType(Field field) throws DediException {
        return resolver.resolve(field).getType();
    }
    
    public Object getInstance(Field field) throws DediException {
        return resolver.resolve(field).getInstance();
    }
    
    @SuppressWarnings("serial")
    private void checkForCycles() throws DediException {
        for (Source source : sources.values()) {
            final Class<?> clazz = source.getType();
            checkForCycles(new HashSet<Class<?>>() {
                {
                    add(clazz);
                }
            }, clazz);
        }
    }

    private void checkForCycles(Set<Class<?>> existing, Class<?> target)
        throws DependencyCycleException {

        for (Field field : target.getDeclaredFields()) {            
            if (field.getAnnotation(Autowired.class) != null) {
                Class<?> type = field.getType();
                
                if (existing.contains(type)) {
                    throw new DependencyCycleException(field, target);
    
                } else {
                    existing.add(type);
                    checkForCycles(existing, type);
                }
            }
        }
    }
}
