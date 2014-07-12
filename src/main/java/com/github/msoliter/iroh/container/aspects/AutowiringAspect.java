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
import com.github.msoliter.iroh.container.resolvers.QualifiedDependencyResolver;
import com.github.msoliter.iroh.container.resolvers.SubclassDependencyResolver;
import com.github.msoliter.iroh.container.services.Injector;
import com.github.msoliter.iroh.container.services.Registrar;

@Aspect
public class AutowiringAspect {
        
    private final Registrar registrar = new Registrar(
        new QualifiedDependencyResolver(),
        new SubclassDependencyResolver());
    
    private final Injector injector = new Injector(registrar);

    private AutowiringAspect() { 
        
        // Hopefully, this gets garbage collected...
        Reflections reflections = new Reflections("");
        
        for (Class<?> type : reflections.getTypesAnnotatedWith(Component.class)) {
            registrar.register(type);
        }
    }

    @Pointcut("execution(*.new(..))")
    public void construction() { }
    
    @Pointcut("cflow(within(!com.github.msoliter.iroh.container..*))")
    public void external() { }

    @Pointcut("get(@com.github.msoliter.iroh.container.annotations.Autowired * *)")
    public void access() { }
    
    @Before("external() && construction() && this(target)")
    public void eagerlyInjectObject(Object target) {        
        for (Field field : target.getClass().getDeclaredFields()) {
            injector.eagerlyInject(target, field);             
        }
    }

    @Before("external() && access()")
    public void lazilyInjectField(JoinPoint thisJoinPoint) {
        FieldSignature fs = (FieldSignature) thisJoinPoint.getSignature();
        Field field = fs.getField();
        Object target = thisJoinPoint.getTarget();
        injector.lazilyInject(target, field);
    }   
}
