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
package com.github.msoliter.iroh.container.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * States that the annotated type should be injectable via its zero argument
 * constructor, or that the annotated method produces an object that should
 * be injectable by calling that method with no parameters.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Component {
    
    /* This component's qualifier, if its supertype has many implementations. */
    public String qualifier() default "";

    /* The scope of the component - 'singleton' or 'prototype'. */
    public String scope() default "singleton";
    
    /* Whether or not this component should override an existing definition of
     * a component with the same type. Primarily used for unit testing. */
    public boolean override() default false;
}
