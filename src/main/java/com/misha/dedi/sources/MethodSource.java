package com.misha.dedi.sources;

import java.lang.reflect.Method;

import com.misha.dedi.annotations.Component;
import com.misha.dedi.exceptions.NonConcreteComponentClassException;

public class MethodSource extends Source {

    private final Method method;
    
    private final Source declarer;
    
    public MethodSource(Source declarer, Method method) 
        throws NonConcreteComponentClassException {
        
        super(method.getAnnotation(Component.class), method.getReturnType());
        this.method = method;
        this.declarer = declarer;
    }

    @Override
    protected Object doGetInstance() throws Exception {
        return method.invoke(declarer.getInstance());
    }
}
