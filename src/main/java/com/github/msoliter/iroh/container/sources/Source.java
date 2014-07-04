package com.github.msoliter.iroh.container.sources;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import com.github.msoliter.iroh.container.annotations.Component;
import com.github.msoliter.iroh.container.exceptions.FailedConstructionException;
import com.github.msoliter.iroh.container.exceptions.NonConcreteComponentClassException;

public abstract class Source {

    private final Class<?> type;
    
    private final boolean prototype;
    
    private final String qualifier;
    
    private final boolean override;
    
    private final Map<Class<?>, Object> cache = new HashMap<>();
    
    protected Source(Component component, Class<?> type) 
        throws NonConcreteComponentClassException {
        
        this.type = type;
        this.prototype = component.scope().equals("prototype");
        this.qualifier = component.qualifier();
        this.override = component.override();
        
        if (!isConcrete(type.getModifiers())) {
            throw new NonConcreteComponentClassException(type);
        }
    }
    
    public Object getInstance() {
        try {
            if (prototype) {
                return doGetInstance();
                
            } else {
                synchronized (cache) {
                    if (!cache.containsKey(type)) {
                        cache.put(type, doGetInstance());
                    }
                    
                    return cache.get(type);
                }
            }
            
        } catch (RuntimeException e) {
            throw e;
            
        } catch (Exception e) {
            throw new FailedConstructionException(e);
        }
    }

    protected abstract Object doGetInstance() throws Exception;
    
    public final String getQualifier() {
        return qualifier;
    }
    
    public final Class<?> getType() {
        return type;
    }
    
    public final boolean isOverriding() {
        return override;
    }
    
    private final static boolean isConcrete(int modifiers) {
        return
            !Modifier.isAbstract(modifiers) &&
            !Modifier.isInterface(modifiers);
    }
}
