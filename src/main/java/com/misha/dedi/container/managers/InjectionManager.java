package com.misha.dedi.container.managers;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import com.misha.dedi.container.annotations.Autowired;
import com.misha.dedi.container.exceptions.DediException;

public class InjectionManager {
    
    private final ResolutionManager resolver;

    private final Map<Class<?>, Object> nulls = new HashMap<>();

    private final Objenesis objenesis = new ObjenesisStd(true);

    public InjectionManager(ResolutionManager resolver) throws DediException {         
        this.resolver = resolver;
    }

    public void eagerlyInject(Object target, Field field) 
        throws DediException {
        
        if (field.getAnnotation(Autowired.class) != null) {
            field.setAccessible(true);
            Autowired autowired = field.getAnnotation(Autowired.class);
            
            if (autowired.lazy() == false) {           
                inject(field, target);   
                
            } else {
                nullify(field, target);
            } 
        }
    }
    
    public void lazilyInject(Object target, Field field) 
        throws DediException {    
        
        field.setAccessible(true);
        
        if (isNullified(field, target)) {
            inject(field, target);            
        }
    }
    
    private void inject(Field field, Object target) 
        throws DediException {

        try {
            field.set(target, resolver.resolve(field).getInstance());
        
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(e);        
        }
    }
    
    private boolean isNullified(Field field, Object target) {
        Class<?> type = field.getType();
        Object value = null;
        
        try {
            value = field.get(target);
            
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        
        if (nulls.containsKey(type)) {
            if (nulls.get(type) == value) {
                return true;
            }
        }
        
        return false;
    }
    
    private void nullify(Field field, Object target) 
        throws DediException {       
        
        Class<?> type = field.getType();
        
        if (!nulls.containsKey(type)) {
            nulls.put(type, objenesis.newInstance(resolver.resolve(field).getType()));
        }
        
        try {
            field.set(target, nulls.get(type));
            
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(e);        
        }
    }
}
