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

import com.github.msoliter.iroh.container.exceptions.base.IrohException;

/**
 * Represents an exception thrown when an 
 * {@link com.github.msoliter.iroh.container.annotations.Autowired} type did 
 * not have a suitable constructor. In general, that means the no-argument 
 * constructor was missing or otherwise blocked (in the case of a hard-coded 
 * constructor or an enumeration).
 */
public class NoZeroArgumentConstructorException extends IrohException {

    /* This exception's serial number. */
    private static final long serialVersionUID = -8108787029867746306L;
    
    /* This exception's error message format. */
    private static final String format = 
        "No zero argument constructor was found for type %s.";
    
    /**
     * Constructs an exception for the given type.
     * 
     * @param type The 
     *  {@link com.github.msoliter.iroh.container.annotations.Autowired} type 
     *  lacking a zero-argument constructor.
     */
    public NoZeroArgumentConstructorException(Class<?> type) {
        super(String.format(format, type));
    }
}
