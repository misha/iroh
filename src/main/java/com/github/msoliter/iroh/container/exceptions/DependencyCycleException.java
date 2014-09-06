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
package com.github.msoliter.iroh.container.exceptions;

import java.util.List;

import com.github.msoliter.iroh.container.exceptions.base.IrohException;
import com.google.common.base.Joiner;

/**
 * Thrown when a dependency cycle has been detected in Iroh' object graph.
 */
public class DependencyCycleException extends IrohException {

    /* This exception's serial number. */
    private static final long serialVersionUID = -3509443257035290026L;
    
    /* This exception's error message format. */
    private static final String format = "A dependency cycle was detected: %s.";
    
    /**
     * Constructs an exception from a dependency stack trace.
     * 
     * @param trace The ordered list of dependency types, where each type has
     *  an field dependency of the previous type. The first and last types in 
     *  the trace must be the same, by definition of a cycle.
     */
    public DependencyCycleException(List<Class<?>> trace) {
        super(String.format(format, Joiner.on(" => ").join(trace)));
    }
}
