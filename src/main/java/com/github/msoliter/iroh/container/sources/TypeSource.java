package com.github.msoliter.iroh.container.sources;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import com.github.msoliter.iroh.container.annotations.Component;
import com.github.msoliter.iroh.container.exceptions.NoZeroArgumentConstructorException;
import com.github.msoliter.iroh.container.exceptions.NonConcreteComponentClassException;

public class TypeSource extends Source {
    
    public TypeSource(Class<?> type) throws NonConcreteComponentClassException {
        super(type.getAnnotation(Component.class), type);

        if (!isConcrete(type.getModifiers())) {
            throw new NonConcreteComponentClassException(type);
        }
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
    
    private final static boolean isConcrete(int modifiers) {
        return
            !Modifier.isAbstract(modifiers) &&
            !Modifier.isInterface(modifiers);
    }
}
