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
}
