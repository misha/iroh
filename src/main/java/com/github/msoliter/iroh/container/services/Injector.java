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
package com.github.msoliter.iroh.container.services;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import com.github.msoliter.iroh.container.annotations.Autowired;
import com.github.msoliter.iroh.container.exceptions.base.IrohException;

public class Injector {
    
    private final Registrar registrar;

    private final Map<Class<?>, Object> nulls = new HashMap<>();

    private final Objenesis objenesis = new ObjenesisStd(true);

    public Injector(Registrar registrar) {         
        this.registrar = registrar;
    }

    public void eagerlyInject(Object target, Field field) {      
        if (field.getAnnotation(Autowired.class) != null) {
            field.setAccessible(true);
            Autowired autowired = field.getAnnotation(Autowired.class);
            
            if (autowired.lazy() == false) {           
                inject(field, target);   
                
            } else {
                nullify(field, target);
            } 
        }
    }
    
    public void lazilyInject(Object target, Field field) {        
        field.setAccessible(true);
        
        if (isNullified(field, target)) {
            inject(field, target);            
        }
    }
    
    private void inject(Field field, Object target) {
        try {
            field.set(target, registrar.resolve(field).getInstance());
        
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new IrohException(e);        
        }
    }
    
    private boolean isNullified(Field field, Object target) {
        Class<?> type = field.getType();
        Object value = null;
        
        try {
            value = field.get(target);
            
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new IrohException(e);
        }
        
        if (nulls.containsKey(type)) {
            if (nulls.get(type) == value) {
                return true;
            }
        }
        
        return false;
    }
    
    private void nullify(Field field, Object target) {              
        Class<?> type = field.getType();
        
        if (!nulls.containsKey(type)) {
            nulls.put(
                type, 
                objenesis.newInstance(registrar.resolve(field).getType()));
        }
        
        try {
            field.set(target, nulls.get(type));
            
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new IrohException(e);        
        }
    }
}
