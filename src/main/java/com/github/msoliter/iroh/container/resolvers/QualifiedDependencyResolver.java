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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.github.msoliter.iroh.container.annotations.Autowired;
import com.github.msoliter.iroh.container.exceptions.DuplicateQualifierException;
import com.github.msoliter.iroh.container.exceptions.NoSuchQualifierException;
import com.github.msoliter.iroh.container.resolvers.base.DependencyResolver;
import com.github.msoliter.iroh.container.sources.base.Source;

public class QualifiedDependencyResolver implements DependencyResolver {

    private final Map<String, Source> qualified = new ConcurrentHashMap<>();
    
    public void register(Source source) {
        String qualifier = source.getQualifier();
        
        if (!qualifier.equals("")) {
            if (qualified.containsKey(qualifier)) {
                throw new DuplicateQualifierException(
                    qualifier,
                    qualified.get(qualifier).getType(),
                    source.getType());
            
            } else {
                qualified.put(source.getQualifier(), source);
            }
        }
    }
    
    @Override
    public Source resolve(Field field) {      
        Autowired autowired = field.getAnnotation(Autowired.class);
        String qualifier = autowired.qualifier();
        
        if (!qualifier.equals("")) {
            Source source = qualified.get(qualifier);

            if (source == null) {
                throw new NoSuchQualifierException(qualifier);
            }
            
            return source;
        }
        
        return null;
    }
}
