package com.misha.dedi.container.sources;

import java.lang.reflect.Constructor;

import com.misha.dedi.container.annotations.Component;
import com.misha.dedi.container.exceptions.NoZeroArgumentConstructorException;
import com.misha.dedi.container.exceptions.NonConcreteComponentClassException;

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
