package com.misha.dedi.objects;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

import com.misha.dedi.annotations.Component;
import com.misha.dedi.exceptions.BadAutowiredTypeException;
import com.misha.dedi.exceptions.DediException;
import com.misha.dedi.exceptions.FailedConstructionException;
import com.misha.dedi.exceptions.NoZeroArgumentConstructorException;

/**
 * Abstracts away the difference between a method and a type as components.
 */
public class ComponentSource {
    
    private Class<?> clazz;
    
    private Object declarer;
    
    private Method method;
    
    private Map<Class<?>, Object> cache;
    
    private Component annotation;
    
    public ComponentSource(Class<?> clazz, Map<Class<?>, Object> cache) {
        this.clazz = clazz;
        this.cache = cache;
        this.annotation = clazz.getAnnotation(Component.class);
    }
    
    public ComponentSource(Object declarer, Method method, Map<Class<?>, Object> cache) {
        this.declarer = declarer;
        this.method = method;
        this.cache = cache;
        this.annotation = method.getAnnotation(Component.class);
    }
    
    public boolean isConcrete() {
        int modifiers = (clazz != null) ? 
            clazz.getModifiers() : 
            method.getModifiers();
            
        return
            !Modifier.isAbstract(modifiers) &&
            !Modifier.isInterface(modifiers);
    }
    
    public Class<?> getType() {
        if (clazz != null) {
            return clazz;
            
        } else {
            return method.getReturnType();
        }
    }
    
    /**
     * Precondition: this type is concrete.
     * 
     * @return An instance of this type.
     * @throws DediException If we failed to build an instance somehow.
     */
    public Object getInstance() throws DediException {
        try {
            if (clazz != null) {
                
                /**
                 * Get an instance from the stored class. 
                 */
                Constructor<?> constructor = null;
                
                try {
                    constructor = clazz.getConstructor();
                    
                } catch (NoSuchMethodException e) {
                    throw new NoZeroArgumentConstructorException(clazz);
                }
    
                boolean accessibility = constructor.isAccessible();
                constructor.setAccessible(true);
            
                if (annotation.scope().equals("prototype")) {
                    try {
                        return constructor.newInstance();
                    
                    } catch (Exception e) {
                        throw e;
                        
                    } finally {
                        constructor.setAccessible(accessibility);
                    }
                        
                } else {
                    if (cache.containsKey(clazz)) {
                        return cache.get(clazz);
                        
                    } else {
                        Object instance = constructor.newInstance();
                        cache.put(clazz, instance);
                        return instance;
                    }
                }
            
            } else {
                
                /**
                 * Get an instance from the stored method.
                 */
                Class<?> type = method.getReturnType();
    
                if (annotation.scope().equals("prototype")) {
                    return method.invoke(declarer);
                    
                } else {
                    if (cache.containsKey(type)) {
                        return cache.get(type);
                        
                    } else {
                        Object instance = method.invoke(declarer);
                        cache.put(clazz, instance);
                        return instance;
                    }
                }
            }
            
        } catch (InstantiationException e) {
            
            /**
             * This happens when the autowired type is not concrete,
             * an array type, a primitive type, or is void.
             */
            throw new BadAutowiredTypeException(clazz);
            
        } catch (IllegalAccessException e) {
            
            /**
             * Should never happen because we flip the accessible bit.
             */
            throw new RuntimeException(e);
            
        } catch (IllegalArgumentException e) {
            
            /**
             * Should never happen because we pass no arguments.
             */
            throw new RuntimeException(e);
            
        } catch (InvocationTargetException e) {
            
            /**
             * Occurs when the constructor for the instance itself
             * throws an exception.
             */
            throw new FailedConstructionException(e);   
        }
    }
}