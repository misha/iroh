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
package com.github.msoliter.iroh.container.managers;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Stack;

import com.github.msoliter.iroh.container.annotations.Autowired;
import com.github.msoliter.iroh.container.annotations.Component;
import com.github.msoliter.iroh.container.exceptions.DependencyCycleException;
import com.github.msoliter.iroh.container.exceptions.UnresolvableFieldException;
import com.github.msoliter.iroh.container.resolvers.DependencyResolver;
import com.github.msoliter.iroh.container.sources.MethodSource;
import com.github.msoliter.iroh.container.sources.Source;
import com.github.msoliter.iroh.container.sources.TypeSource;

public class ResolutionManager {

    private final DependencyResolver[] resolvers;
    
    public ResolutionManager(DependencyResolver... resolvers) {
        this.resolvers = resolvers;
    }
    
    public void register(Class<?> type) {
        Source typeSource = new TypeSource(type);
        register(typeSource);

        for (Method method : type.getMethods()) {
            Component annotation = method.getAnnotation(Component.class);
            
            if (annotation != null) {
                register(new MethodSource(typeSource, method));
            }
        }
        
        checkForCycles(type);
    }

    public Source resolve(Field field) {
        for (DependencyResolver resolver : resolvers) {
            Source source = resolver.resolve(field);
            
            if (source != null) {
                return source;
            }
        }
        
        throw new UnresolvableFieldException(field);
    }
    
    private void register(Source source) {
        for (DependencyResolver resolver : resolvers) {
            resolver.register(source);
        }
    }
    
    private void checkForCycles(Class<?> target) throws DependencyCycleException {
        Stack<Class<?>> trace = new Stack<>();
        trace.push(target);
        checkForCycles(target, target, trace);
    }
    
    private void checkForCycles(Class<?> target, Class<?> in, Stack<Class<?>> trace) 
        throws DependencyCycleException {
        
        for (Field field : in.getDeclaredFields()) {  
            if (field.getAnnotation(Autowired.class) != null) {
                Class<?> type = field.getType();
                trace.push(type);
                
                if (type.equals(target)) {
                    throw new DependencyCycleException(trace);       
                    
                } else {
                    checkForCycles(target, type, trace);
                }
                
                trace.pop();
            }
        }    
    }
}
