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
package com.github.msoliter.iroh.container.aspects;

import java.lang.reflect.Field;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.FieldSignature;
import org.reflections.Reflections;

import com.github.msoliter.iroh.container.annotations.Component;
import com.github.msoliter.iroh.container.resolvers.QualifiedResolver;
import com.github.msoliter.iroh.container.resolvers.SubclassResolver;
import com.github.msoliter.iroh.container.services.Injector;
import com.github.msoliter.iroh.container.services.Registrar;

/**
 * The aspect responsible for hooking into constructor calls and field accesses
 * so that the dependency injection container may perform the correct operations
 * at the required locations.
 */
@Aspect
public class IrohAspect {
        
    /* A registrar responsible for managing all registered components. */
    private final Registrar registrar = new Registrar(
        new QualifiedResolver(),
        new SubclassResolver());
    
    /* An injector featuring lazy and eager injection methods. */
    private final Injector injector = new Injector(registrar);

    /**
     * Constructs the Iroh aspect by scanning the class path for 
     * {@link com.github.msoliter.iroh.container.annotations.Component} 
     * annotations and registering them for injection.
     */
    private IrohAspect() {         
        Reflections reflections = new Reflections("");
        
        for (Class<?> type : reflections.getTypesAnnotatedWith(Component.class)) {
            registrar.register(type);
        }
    }

    /**
     * A pointcut representing the start of execution of a new constructor.
     */
    @Pointcut("execution(*.new(..))")
    public void construction() { }
    
    /**
     * A pointcut representing any operation outside the control flow of Iroh.
     */
    @Pointcut("cflow(within(!com.github.msoliter.iroh.container..*))")
    public void external() { }

    /**
     * A pointcut representing any field access of a field carrying an
     * {@link com.github.msoliter.iroh.container.annotations.Autowired} 
     * annotation.
     */
    @Pointcut("get(@com.github.msoliter.iroh.container.annotations.Autowired * *)")
    public void access() { }
    
    /**
     * Aspect advice triggered prior to the construction of all objects. If the
     * type contains any 
     * {@link com.github.msoliter.iroh.container.annotations.Autowired} fields 
     * not marked for lazy injection, this advice will properly initialize 
     * those fields prior to the constructor's code.
     * 
     * @param target The target of construction.
     */
    @Before("external() && construction() && this(target)")
    public void eagerlyInjectObject(Object target) {        
        for (Field field : target.getClass().getDeclaredFields()) {
            injector.eagerlyInject(target, field);             
        }
    }

    /**
     * Aspect advice triggered on the access of all fields carrying an
     * {@link com.github.msoliter.iroh.container.annotations.Autowired} 
     * annotation. If the field has been marked for lazy injection, and has not 
     * been injected with an instance yet, this advice will properly initialize 
     * that field.
     * 
     * @param thisJoinPoint The join point carrying critical information about
     *  which field is being accessed, as well as its current contents.
     */
    @Before("external() && access()")
    public void lazilyInjectField(JoinPoint thisJoinPoint) {
        FieldSignature fs = (FieldSignature) thisJoinPoint.getSignature();
        Field field = fs.getField();
        Object target = thisJoinPoint.getTarget();
        injector.lazilyInject(target, field);
    }   
}
