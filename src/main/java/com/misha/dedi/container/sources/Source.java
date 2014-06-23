package com.misha.dedi.container.sources;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import com.misha.dedi.container.annotations.Component;
import com.misha.dedi.container.exceptions.DediException;
import com.misha.dedi.container.exceptions.FailedConstructionException;
import com.misha.dedi.container.exceptions.NonConcreteComponentClassException;

public abstract class Source {
        
    private final Class<?> type;
    
    private final boolean prototype;
    
    private final String qualifier;
    
    private final Map<Class<?>, Object> cache = new HashMap<>();
    
    protected Source(Component component, Class<?> type) 
        throws NonConcreteComponentClassException {
        
        this.type = type;
        this.prototype = component.scope().equals("prototype");
        this.qualifier = component.qualifier();
        
        if (!isConcrete(type.getModifiers())) {
            throw new NonConcreteComponentClassException(type);
        }
    }
    
    public Object getInstance() throws DediException {
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
            
        } catch (DediException e) {
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
    
    private final static boolean isConcrete(int modifiers) {
        return
            !Modifier.isAbstract(modifiers) &&
            !Modifier.isInterface(modifiers);
    }
}
