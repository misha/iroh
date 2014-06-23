package com.misha.dedi.container.managers;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import com.misha.dedi.container.annotations.Autowired;
import com.misha.dedi.container.annotations.Component;
import com.misha.dedi.container.exceptions.DediException;
import com.misha.dedi.container.exceptions.DependencyCycleException;
import com.misha.dedi.container.exceptions.UnresolvableFieldException;
import com.misha.dedi.container.resolvers.DependencyResolver;
import com.misha.dedi.container.sources.MethodSource;
import com.misha.dedi.container.sources.Source;
import com.misha.dedi.container.sources.TypeSource;

public class ResolutionManager {

    private final DependencyResolver[] resolvers;
    
    public ResolutionManager(DependencyResolver... resolvers) {
        this.resolvers = resolvers;
    }
    
    public void register(Class<?> type) throws DediException {
        Source typeSource = new TypeSource(type);
        register(typeSource);

        for (Method method : type.getMethods()) {
            Component annotation = method.getAnnotation(Component.class);
            
            if (annotation != null) {
                register(new MethodSource(typeSource, method));
            }
        }
        
        checkForCycles(type);
    }

    public Source resolve(Field field) throws DediException {
        for (DependencyResolver resolver : resolvers) {
            Source source = resolver.resolve(field);
            
            if (source != null) {
                return source;
            }
        }
        
        throw new UnresolvableFieldException(field);
    }
    
    private void register(Source source) {
        for (DependencyResolver resolver : resolvers) {
            resolver.register(source);
        }
    }
    
    @SuppressWarnings("serial")
    private void checkForCycles(final Class<?> clazz) throws DediException {
        checkForCycles(new HashSet<Class<?>>() {
            {
                add(clazz);
            }
        }, clazz); 
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
