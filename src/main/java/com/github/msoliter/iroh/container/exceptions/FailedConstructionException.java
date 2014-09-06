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
 * Thrown when an error during object instantiation occurs. The most common 
 * cause is an exception thrown in the constructor of a component type.
 */
public class FailedConstructionException extends IrohException {

    /* This exception's serial number. */
    private static final long serialVersionUID = 6874503833663604131L;
    
    /* This exception's error message format. */
    private static final String format = 
        "Injection failed due to an error during object instantiation.";
    
    /**
     * Constructs an exception from the given construction failure cause.
     * 
     * @param cause The exception that caused construction to fail.
     */
    public FailedConstructionException(Throwable cause) {
        super(String.format(format), cause);
    }
}
