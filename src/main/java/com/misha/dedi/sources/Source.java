package com.misha.dedi.sources;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import com.misha.dedi.annotations.Component;
import com.misha.dedi.exceptions.DediException;
import com.misha.dedi.exceptions.FailedConstructionException;
import com.misha.dedi.exceptions.NonConcreteComponentClassException;

public abstract class Source {
        
    private final Class<?> type;
    
    private final boolean concrete;
    
    private final boolean prototype;
    
    private final String qualifier;
    
    private final Map<Class<?>, Object> cache = new HashMap<>();
    
    protected Source(Component component, Class<?> type) 
        throws NonConcreteComponentClassException {
        
        this.type = type;
        this.concrete = isConcrete(type.getModifiers()); 
        this.prototype = component.scope().equals("prototype");
        this.qualifier = component.qualifier();
        
        if (!isConcrete()) {
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
    
    public final boolean isConcrete() {
        return concrete;
    }
    
    private final static boolean isConcrete(int modifiers) {
        return
            !Modifier.isAbstract(modifiers) &&
            !Modifier.isInterface(modifiers);
    }
}
