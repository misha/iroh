package com.misha.dedi.sources;

import java.lang.reflect.Constructor;

import com.misha.dedi.annotations.Component;
import com.misha.dedi.exceptions.NoZeroArgumentConstructorException;
import com.misha.dedi.exceptions.NonConcreteComponentClassException;

public class TypeSource extends Source {
    
    public TypeSource(Class<?> type) throws NonConcreteComponentClassException {
        super(type.getAnnotation(Component.class), type);
    }

    @Override
    public Object doGetInstance() throws Exception {
        try {
            Constructor<?> constructor = getType().getConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
            
        } catch (NoSuchMethodException e) {
            throw new NoZeroArgumentConstructorException(getType());
        }
    }
}
