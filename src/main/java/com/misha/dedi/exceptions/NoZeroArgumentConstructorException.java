package com.misha.dedi.exceptions;

/**
 * Represents an exception thrown when an autowired type did not have a suitable
 * constructor. In general, that means the no-argument constructor was missing
 * or otherwise blocked (in the case of a hard-coded constructor or an enum).
 */
@SuppressWarnings("serial")
public class NoZeroArgumentConstructorException extends Exception {

}
