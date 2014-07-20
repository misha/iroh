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
package com.github.msoliter.iroh.container.sources.base;

import java.util.HashMap;
import java.util.Map;

import com.github.msoliter.iroh.container.annotations.Component;
import com.github.msoliter.iroh.container.annotations.Scope;
import com.github.msoliter.iroh.container.exceptions.FailedConstructionException;
import com.github.msoliter.iroh.container.exceptions.base.IrohException;

/**
 * Represents a source of instances to Iroh's object graph. There is a 
 * one-to-one mapping of sources to concrete types with the
 * {@link com.github.msoliter.iroh.container.annotations.Component} annotation.
 */
public abstract class Source {

    /* This source's underlying type. */
    private final Class<?> type;
    
    /* Whether or not this source generates prototypes or uses a singleton. */
    private final boolean prototype;
    
    /* This source's qualifier, if one exists. */
    private final String qualifier;
    
    /* Whether or not this source overrides other sources with the same type
     * or subtype during subclass resolution. */
    private final boolean override;
    
    /* A simple cache of instances to implement singletons. */
    private final Map<Class<?>, Object> cache = new HashMap<>();
    
    /**
     * Constructs a source using the given annotation and type. Implementations
     * of this type should call this constructor in order to qualify its
     * methods.
     * 
     * @param component The annotation that triggered the creation of this 
     *  source.
     * @param type The type annotated with that annotation.
     */
    protected Source(Component component, Class<?> type) {        
        this.type = type;
        this.prototype = component.scope() == Scope.PROTOTYPE;
        this.qualifier = component.qualifier();
        this.override = component.override();
    }
    
    /**
     * Retrieves an instance of this source's type, respecting scoping rules.
     * 
     * @return An instance of this source's type.
     */
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
            
        } catch (IrohException e) {
            throw e;
            
        } catch (Exception e) {
            throw new FailedConstructionException(e);
        }
    }

    /**
     * Internal version of {@link #getInstance()} that wraps the implementation
     * with error checking and forced scoping respect.
     * 
     * @return An instance of this source's type. Never null.
     * @throws Exception when an error occurred during instantiation, or the
     *  implementor was unable to create an instance for some other reason.
     */
    protected abstract Object doGetInstance() throws Exception;
    
    /**
     * @return This source's qualifier.
     */
    public final String getQualifier() {
        return qualifier;
    }
    
    /**
     * @return This source's type.
     */
    public final Class<?> getType() {
        return type;
    }
    
    /**
     * @return Whether or not this source is overriding with regards to
     *  subclass resolution.
     */
    public final boolean isOverriding() {
        return override;
    }
}
