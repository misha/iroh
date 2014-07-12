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
package com.github.msoliter.iroh.container.resolvers;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.github.msoliter.iroh.container.exceptions.UnexpectedImplementationCountException;
import com.github.msoliter.iroh.container.resolvers.base.DependencyResolver;
import com.github.msoliter.iroh.container.sources.base.Source;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class SubclassDependencyResolver implements DependencyResolver {

    private final Multimap<Class<?>, Source> sources = HashMultimap.create();
    
    @Override
    public void register(Source source) {
        synchronized (sources) {
            sources.put(source.getType(), source);
        }
    }

    @Override
    public Source resolve(Field field) {         
        Set<Source> potential = new HashSet<>();
        Class<?> target = field.getType();
        
        for (Class<?> type : sources.keySet()) {
            if (target.isAssignableFrom(type)) {
                Collection<Source> candidates = null;
                
                synchronized (sources) {
                    candidates = sources.get(type);                    
                }
                
                potential.addAll(candidates);
            }
        }

        if (potential.size() != 1) {
            Set<Source> overriding = new HashSet<>();
            
            for (Source source : potential) {
                if (source.isOverriding()) {
                    overriding.add(source);
                }
            }
            
            if (overriding.size() != 1) {              
                throw new UnexpectedImplementationCountException(
                    target, 
                    Collections2.transform(
                        potential,
                        new Function<Source, Class<?>>() {
                            
                            @Override
                            public Class<?> apply(Source source) {
                                return source.getType();
                            }
                        }));
                
            } else {
                return overriding.iterator().next();
            }
            
        } else {
            return potential.iterator().next();
        }
    } 
}
