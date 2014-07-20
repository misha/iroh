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
package com.github.msoliter.iroh.container.exceptions;

import com.github.msoliter.iroh.container.exceptions.base.IrohException;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;

/**
 * Represents an exception thrown when a base type is 
 * {@link com.github.msoliter.iroh.container.annotations.Autowired}, but there 
 * appears to be more or less than one concrete implementation of that  base 
 * type available for injection.
 */
public class UnexpectedImplementationCountException extends IrohException {

    /* This exception's serial number. */
    private static final long serialVersionUID = 6424073647507088551L;
    
    /* This exception's error message format for zero implementations. */
    private static final String zeroImplementationsFormat =
        "Expected exactly 1 implementation of type %s by found 0.";
    
    /* This exception's error message format for two or more implementations. */
    private static final String multipleImplementationsFormat = 
        "Expected exactly 1 implementation of type %s but found %d: %s.";
    
    /**
     * Constructs an exception detailing exactly the implementations identified
     * for a particular type.
     * 
     * @param type The type we were attempting to resolve.
     * @param implementations A collection of all the implementing types. The
     *  size of the collection should be zero or greater than one.
     */
    public UnexpectedImplementationCountException(
        Class<?> type, 
        Iterable<Class<?>> implementations) {
        
        super(
            (Iterables.size(implementations) == 0) ?
                String.format(zeroImplementationsFormat, type) :
                String.format(
                    multipleImplementationsFormat, 
                    type, 
                    Iterables.size(implementations), 
                    Joiner.on(", ").join(implementations)));
    }
}
