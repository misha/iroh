package com.misha.dedi.aspects;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.FieldSignature;
import org.reflections.Reflections;

import com.misha.dedi.annotations.Autowired;
import com.misha.dedi.annotations.Lazy;
import com.misha.dedi.annotations.Prototype;
import com.misha.dedi.annotations.Qualifier;
import com.misha.dedi.exceptions.DediException;
import com.misha.dedi.exceptions.NoSuchQualifierException;
import com.misha.dedi.exceptions.NoZeroArgumentConstructorException;
import com.misha.dedi.exceptions.UnexpectedImplementationCountException;

@Aspect
public class AutowiringAspect {
        
    private final Map<Class<?>, Object> instances = new HashMap<>();
    
    private final Map<String, Class<?>> qualified = new HashMap<>();
    
    private final Map<Class<?>, Class<?>> implementations = new HashMap<>();
    
    private final Reflections reflections = new Reflections("");
    
    private final Logger log = Logger.getLogger("dedi");
    
    public AutowiringAspect() {
        log.info("Initializing the autowiring aspect.");
        
        /**
         * Initialize the set of all qualified types.
         */
        Set<Class<?>> qualifiedTypes = 
            reflections.getTypesAnnotatedWith(Qualifier.class);
        
        for (Class<?> type : qualifiedTypes) {
            Qualifier annotation = type.getAnnotation(Qualifier.class);
            qualified.put(annotation.value(), type);
            log.info("Registered qualifier \"" + annotation.value() + "\" for " + type);
        }
    }
    
    @Pointcut("execution((!AutowiringAspect).new(..))")
    public void onConstruction() {
        
    }
                
    @Pointcut(
        "get(@com.misha.dedi.annotations.Autowired * *) && " +
        "@annotation(annotation)")
    public void onFieldAccess(Autowired annotation) { 
        
    }
    
    /**
     * Injects dependencies into an object prior to the constructor.
     */
    @Before("onConstruction() && this(object)")
    public void eagerlyInject(Object object) throws DediException {
        for (Field field : object.getClass().getFields()) {
            Autowired autowired = field.getAnnotation(Autowired.class);
            
            /**
             * Only inject an autowired field if it's not marked 'lazy'.
             */
            if (autowired != null && field.getAnnotation(Lazy.class) != null) {           
                inject(autowired, field, object);
            }                  
        }
    }

    /**
     * Injects dependencies into a field prior to accessing the field.
     */
    @Before("onFieldAccess(annotation)")
    public void lazilyInject(
        Autowired annotation, 
        JoinPoint thisJoinPoint) 
        throws DediException {
        
        /**
         * Figure out which field needs to get injected. If we allow the
         * autowired annotation on things other than fields, then this
         * cast will have to be refactored into a lookup.
         */
        FieldSignature fs = (FieldSignature) thisJoinPoint.getSignature();
        Field field = fs.getField();

        /**
         * Get the target field instance, and the type of object that needs
         * to be injected into the field instance.
         */
        Object target = thisJoinPoint.getTarget();
        inject(annotation, field, target);
    }   
    
    private void inject(
        Autowired annotation, 
        Field field, 
        Object target) 
        throws DediException {
        
        /**
         * Change the field's accessibility so that no access exceptions
         * are thrown when we inject the actual value.
         */
        boolean accessibility = field.isAccessible();
        field.setAccessible(true);
        
        /**
         * Only inject if the current value is null.
         */
        try {
            if (field.get(target) == null) {
             
                /**
                 * Figure out the correct type to use for injection, in case any
                 * qualifiers were triggered during static initialization.
                 */
                Class<?> type = null;
                
                if (annotation.value().equals("")) {
                    type = field.getType();
                    
                } else {
                    type = qualified.get(annotation.value());
                    
                    /**
                     * Validate that we got an actual type from the qualified list.
                     */
                    if (type == null) {
                        throw new NoSuchQualifierException(annotation.value());
                    }
                }
                
                /**
                 * However, even if the type is defined by the qualifier, it still
                 * might be abstract. In general, the type might be abstract anyway
                 * just to hide the implementation details. Resolve the abstract
                 * or interface type into an actual implementation.
                 */
                if (Modifier.isInterface(type.getModifiers()) || 
                    Modifier.isAbstract(type.getModifiers())) {
                    
                    /**
                     * Finding the implementing type is expensive, so let's check
                     * our implementations cache first.
                     */
                    if (implementations.containsKey(type)) {
                        type = implementations.get(type);
                        
                    } else {
                        
                        /**
                         * Find all the subtypes of the given type. This might 
                         * take a while...
                         */
                        @SuppressWarnings("unchecked")
                        Set<Class<? extends Object>> subtypes = 
                            reflections.getSubTypesOf((Class<Object>) type);
                        
                        /**
                         * There must be exactly one concrete subtype at this point.
                         */
                        Iterator<Class<?>> subtypesIterator = subtypes.iterator();
                        
                        while (subtypesIterator.hasNext()) {
                            int modifiers = subtypesIterator.next().getModifiers();
                            
                            /**
                             * Not a concrete type, so throw it out.
                             */
                            if (Modifier.isAbstract(modifiers) ||
                                Modifier.isInterface(modifiers)) {
                                
                                subtypesIterator.remove();
                            }
                        }
   
                        /**
                         * Now our set's size is actually valid - all concrete types.
                         */
                        if (subtypes.size() != 1) {
                            throw new UnexpectedImplementationCountException(type, subtypes.size());
                        }
                        
                        /**
                         * Cache it for later.
                         */
                        Class<?> implementation = subtypes.iterator().next();
                        log.info("Resolved abstract type " + type + " into implementing type " + implementation);
                        implementations.put(type, implementation);
                        type = implementation;
                    }
                }
   
                /**
                 * Inject a new instance for the field, checking for the prototype
                 * annotation as necessary. The default scoping policy is singleton.
                 */
                try {
                    if (type.getAnnotation(Prototype.class) != null) {
                        log.info("Instantiating prototype for " + type);
                        field.set(target, type.getConstructor().newInstance());
                        
                    } else {
                        if (!instances.containsKey(type)) {
                            log.info("Instantiating singleton for " + type);
                            instances.put(type, type.getConstructor().newInstance());
                        }
                        
                        field.set(target, instances.get(type));
                    }
   
                } catch (NoSuchMethodException e) {
                    throw new NoZeroArgumentConstructorException(type);
                    
                } catch (Exception e) {
                    
                    /**
                     * TODO: decipher the errors caused by the other exceptions.
                     */
                    throw new RuntimeException(e);
                }
            }
            
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        /**
         * Reset accessibility to avoid missing future access exceptions.
         */
        field.setAccessible(accessibility); 
    }
}
