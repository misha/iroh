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
import com.google.common.base.Joiner;

/**
 * Thrown when two 
 * {@link com.github.msoliter.iroh.container.annotations.Component}-annotated 
 * types are designated with the same qualifier.
 */
public class DuplicateQualifierException extends IrohException {

    /* This exception's serial number. */
    private static final long serialVersionUID = 7399991728626888167L;
    
    /* This exception's error message format. */
    private static final String format = 
        "The qualifier '%s' was placed on multiple types: %s.";
    
    /**
     * Constructs an exception for the given qualifier.
     * 
     * @param qualifier The qualifier that was detected on multiple types.
     * @param types An array of two or more types with this qualifier.
     */
    public DuplicateQualifierException(String qualifier, Class<?>... types) {
        super(String.format(format, qualifier, Joiner.on(", ").join(types)));
    }
}
