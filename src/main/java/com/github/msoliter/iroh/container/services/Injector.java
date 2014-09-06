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
package com.github.msoliter.iroh.container.services;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import com.github.msoliter.iroh.container.annotations.Autowired;
import com.github.msoliter.iroh.container.exceptions.base.IrohException;

/**
 * Iroh's injector, responsible for producing instances of types during lazy or
 * eager injection. It corresponds directly to the injection annotation, 
 * {@link com.github.msoliter.iroh.container.annotations.Autowired}.
 */
public class Injector {
    
    /* A registrar capable of resolving specific types into sources. */
    private final Registrar registrar;

    /* The map of all "null" objects, which are merely location placeholders
     * used by the injector to mark fields ignored during eager injection and
     * eligible for lazy injection upon the first field access. */
    private final Map<Class<?>, Object> nulls = new HashMap<>();

    /* An incredibly violent but cool library capable of generating "blank"
     * instances of virtually any type. Used to instantiate null instances of
     * every type requiring injection. It places no constraints on the type,
     * whatsoever. We do not ask it to cache its objects because the injector's
     * null map does precisely that for us. */
    private final Objenesis objenesis = new ObjenesisStd(false);

    /**
     * Constructs an injector that relies on the given registrar for resolving 
     * types into instance sources.
     * 
     * @param registrar The registrar to be used for type resolution by this
     *  injector.
     */
    public Injector(Registrar registrar) {         
        this.registrar = registrar;
    }

    /**
     * Eagerly injects the target object's target field. Note that if the field
     * is marked for lazy injection, we still inject it, but with a "null"
     * reference. This reference is never seen by user code, and is used 
     * internally by Iroh to detect when eager injection has delegated the
     * injection process to lazy injection, thus preventing multiple injection.
     * 
     * @param target The target object containing the field to be injected.
     * @param field The target field to be injected in the target object.
     */
    public void eagerlyInject(Object target, Field field) {   
        
        /**
         * We only ever inject fields marked with the correct annotation.
         */
        if (field.getAnnotation(Autowired.class) != null) {
            field.setAccessible(true);
            Autowired autowired = field.getAnnotation(Autowired.class);
            
            /**
             * If the field isn't marked for lazy injection, go ahead and inject
             * it immediately. Otherwise, nullify it with a local "null"
             * reference of the field's required type.
             */
            if (autowired.lazy() == false) {           
                inject(target, field);   
                
            } else {
                nullify(target, field);
            } 
        }
    }
    
    /**
     * Lazily injects the target object's target field. 
     * 
     * @param target The target object containing the field to be injected.
     * @param field The target field to be injected in the target object.
     */
    public void lazilyInject(Object target, Field field) {        
        field.setAccessible(true);
        
        /**
         * We don't need to check for an 
         * {@link com.github.msoliter.iroh.container.annotations.Autowired}
         * annotation because only those fields are capable of having a 
         * reference to one of our local "null" references to begin with. 
         * Directly checking if the object in the field and the null point to
         * the same location is sufficient for determining whether or not to
         * inject the field.
         */
        if (isNullified(target, field)) {
            inject(target, field);            
        }
    }
    
    /**
     * Performs the actual instance injection.
     * 
     * @param target The target object containing the field to be injected.
     * @param field The target field to be injected in the target object.
     */
    private void inject(Object target, Field field) {
        try {
            
            /**
             * Injection is actually quite a simple process. We resolve the
             * field into a source of instances, and ask it to generate one.
             */
            field.set(target, registrar.resolve(field).getInstance());
        
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new IrohException(e);        
        }
    }
    
    /**
     * Checks whether or not the field value object's reference points to one
     * of our local null object references. If so, this indicates that the
     * field was nullified during eager injection, and is now eligible for lazy
     * injection.
     * 
     * @param target The target object containing the field to be checked.
     * @param field The target field who object reference will be checked.
     * @return True if the field's object reference is one of our local nulls,
     *  false otherwise.
     */
    private boolean isNullified(Object target, Field field) {
        Class<?> type = field.getType();
        Object value = null;
        
        try {
            value = field.get(target);
            
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new IrohException(e);
        }
        
        if (nulls.containsKey(type)) {
            
            /**
             * Very purposefully compare the locations rather than their values,
             * since we totally cheated with Objenesis obtaining instances of
             * these local nulls. All hell would likely break loose if we tried
             * to use the methods defined in the actual objects...
             */
            if (nulls.get(type) == value) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Implements lazy injection by filling in a "local null" instance of the
     * type required by the field. The original idea was to just define a new
     * location of null itself, but Java didn't like that very much, so now Iroh
     * will generate a reference for every type it encounters to act as the
     * null for fields of that particular type.
     * 
     * @param target The target object containing the field to be nullified via
     *  injection with a local null generated with Objenesis.
     * @param field The target field in the target object to be nullified.
     */
    private void nullify(Object target, Field field) {              
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
