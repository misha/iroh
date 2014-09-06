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
package com.github.msoliter.iroh.container.exceptions.base;

/**
 * Represents the base class for all Iroh exceptions.
 */
public class IrohException extends RuntimeException {
    
    /* This exception's serial number. */
    private static final long serialVersionUID = -4591906383960858790L;

    /**
     * Builds a non-specific exception caused by an exception unrelated to
     * Iroh's internal workings.
     * 
     * @param cause The cause of this exception.
     */
    public IrohException(Throwable cause) {
        super(cause);
    }
    
    /**
     * Used by extensions of this class to provide access to the exception
     * class' message field. Builds a specific Iroh-based exception.
     * 
     * @param message The message to use for this exception.
     */
    protected IrohException(String message) {
        super(message);
    }
    
    /**
     * Used by extensions of this class to provide access to the exception
     * class' cause and message fields. Builds a specific Iroh-based exception.
     * 
     * @param message The message to use for this exception.
     * @param cause The exception that caused this one to occur.
     */
    protected IrohException(String message, Throwable cause) {
        super(message, cause);
    }
}
