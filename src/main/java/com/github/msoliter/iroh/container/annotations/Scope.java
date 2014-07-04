package com.github.msoliter.iroh.container.annotations;

/**
 * Defines how a dependency will be instantiated.
 */
public enum Scope {
    
    /* A singleton is created exactly once, and then injected into all fields
     * that require that dependency. */
    SINGLETON,
    
    /* A prototype is re-instantiated for each field requiring it as a 
     * dependency. New objects will cause fresh instances to be injected. */
    PROTOTYPE;
}
