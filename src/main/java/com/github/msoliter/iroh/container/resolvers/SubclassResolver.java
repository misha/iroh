/* Copyright 2014 Misha Soliterman
 * 
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

import com.github.msoliter.iroh.container.exceptions.UnexpectedImplementationCountException;
import com.github.msoliter.iroh.container.resolvers.base.Resolver;
import com.github.msoliter.iroh.container.sources.base.Source;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

/**
 * Resolves types into a source by looking up which sources implement that type
 * or a subtype of that type. The result is that users can place
 * {@link com.github.msoliter.iroh.container.annotations.Autowired} on fields
 * whose types are not concrete. Also, note that this class is written in a
 * functional style, which was an elegant way to describe the operations, but
 * does seem more verbose than usual due to Java syntax.
 */
public class SubclassResolver implements Resolver {

    /* Stores all N implementations of every registered type. The primary 
     * reason for using a multimap rather than a normal map is because
     * components may be overridden when testing. */
    private final Multimap<Class<?>, Source> sources = HashMultimap.create();
    
    @Override
    public synchronized void register(Source source) {
        sources.put(source.getType(), source);      
    }

    @Override
    public Source resolve(final Field field) {  
        
        /**
         * Find all potential sources by filter on whether or not a source's
         * type can be assigned to the field's type.
         */
        Iterable<Source> potential = 
            Iterables.concat(
                Collections2.transform(
                    Collections2.filter(
                        sources.keySet(),
                        new Predicate<Class<?>>() {
    
                            @Override
                            public boolean apply(Class<?> type) {
                                return field.getType().isAssignableFrom(type);
                            }                       
                        }),
                    new Function<Class<?>, Collection<Source>>() {
    
                        @Override
                        public Collection<Source> apply(Class<?> type) {
                            return sources.get(type);
                        }                   
                    }));

        /**
         * If there isn't exactly one source in the set, check if there's 
         * exactly one overriding source available, in case we're unit testing.
         */
        if (Iterables.size(potential) != 1) {
            Iterable<Source> overriding = 
                Iterables.filter(
                    potential, 
                    new Predicate<Source>() {

                        @Override
                        public boolean apply(Source source) {
                            return source.isOverriding();
                        }                  
                    });
            
            if (Iterables.size(overriding) != 1) {              
                throw new UnexpectedImplementationCountException(
                    field.getType(), 
                    Iterables.transform(
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
