/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.github.msoliter.iroh.container.sources;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import com.github.msoliter.iroh.container.annotations.Component;
import com.github.msoliter.iroh.container.exceptions.NoZeroArgumentConstructorException;
import com.github.msoliter.iroh.container.exceptions.NonConcreteComponentTypeException;
import com.github.msoliter.iroh.container.sources.base.Source;

/**
 * Represents a source of instances built because of a type carrying an
 * {@link com.github.msoliter.iroh.container.annotations.Component} annotation.
 */
public class TypeSource extends Source {
    
    /**
     * Builds a source from the given type.
     * 
     * @param type The type for which to build a source.
     */
    public TypeSource(Class<?> type) {
        super(type.getAnnotation(Component.class), type);

        /**
         * It's illegal to make sources from non-concrete types because we'll
         * never be able to instantiate them anyway.
         */
        if (!isConcrete(type.getModifiers())) {
            throw new NonConcreteComponentTypeException(type);
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
    
    /**
     * Checks whether or not the modifiers represent a concrete type.
     * 
     * @param modifiers The type's modifiers.
     * @return True if the type is concrete, false otherwise.
     */
    private final static boolean isConcrete(int modifiers) {
        return
            !Modifier.isAbstract(modifiers) &&
            !Modifier.isInterface(modifiers);
    }
}
