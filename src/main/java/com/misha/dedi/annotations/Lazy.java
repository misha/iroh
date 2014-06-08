package com.misha.dedi.annotations;

/**
 * Determines whether or not the annotated field will be injected lazily. By
 * default, fields are loaded eagerly, but in the case of a heavier object that
 * may or may not wind up getting used, this annotation will prevent it from
 * being instantiated until just before it is accessed.
 */
public @interface Lazy {

}
