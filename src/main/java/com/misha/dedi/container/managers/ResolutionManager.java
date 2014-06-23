package com.misha.dedi.container.managers;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Stack;

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
    
    private void register(Source source) throws DediException {
        for (DependencyResolver resolver : resolvers) {
            resolver.register(source);
        }
    }
    
    private void checkForCycles(Class<?> target) throws DependencyCycleException {
        Stack<Class<?>> trace = new Stack<>();
        trace.push(target);
        checkForCycles(target, target, trace);
    }
    
    private void checkForCycles(Class<?> target, Class<?> in, Stack<Class<?>> trace) 
        throws DependencyCycleException {
        
        for (Field field : in.getDeclaredFields()) {            
            if (field.getAnnotation(Autowired.class) != null) {
                Class<?> type = field.getType();
                trace.push(type);
                
                if (type.equals(target)) {
                    throw new DependencyCycleException(trace);       
                    
                } else {
                    checkForCycles(target, type, trace);
                }
                
                trace.pop();
            }
        }    
    }
}
