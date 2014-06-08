package com.misha.dedi.aspects;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.FieldSignature;
import org.aspectj.lang.reflect.SourceLocation;

import com.misha.dedi.annotations.Autowired;
import com.misha.dedi.annotations.Prototype;
import com.misha.dedi.exceptions.NoZeroArgumentConstructorException;

@Aspect
public class LazyAutowiringAspect {
    
    private Set<SourceLocation> injected = new HashSet<>();
    
    private Map<Class<?>, Object> instances = new HashMap<>();
                
    @Pointcut(
        "get(@com.misha.dedi.annotations.Autowired * *) && " +
        "@annotation(annotation)")
    public void autowired(Autowired annotation) { 
        
    }

    @Around("autowired(annotation)")
    public Object resolve(
        Autowired annotation, 
        ProceedingJoinPoint thisJoinPoint) 
        throws Throwable {
        
        /**
         * Don't inject the same field twice, ever.
         */
        if (!injected.contains(thisJoinPoint.getSourceLocation())) {
            injected.add(thisJoinPoint.getSourceLocation());

            /**
             * Figure out which field needs to get injected. If we allow the
             * autowired annotation on things other than fields, then this
             * cast will have to be refactored into a lookup.
             */
            FieldSignature fs = (FieldSignature) thisJoinPoint.getSignature();
            Field field = fs.getField();
            
            /**
             * Change the field's accessibility so that no access exceptions
             * are thrown when we inject the actual value.
             */
            boolean accessibility = field.isAccessible();
            field.setAccessible(true);
            
            /**
             * Get the target field instance, and the type of object that needs
             * to be injected into the field instance.
             */
            Object target = thisJoinPoint.getTarget();
            Class<?> type = field.getType();

            /**
             * Inject a new instance for the field, checking for the prototype
             * annotation as necessary. The default scoping policy is singleton.
             */
            try {
                if (type.getAnnotation(Prototype.class) != null) {
                    field.set(target, type.getConstructor().newInstance());
                    
                } else {
                    if (!instances.containsKey(type)) {
                        instances.put(type, type.getConstructor().newInstance());
                    }
                    
                    field.set(target, instances.get(type));
                }

            } catch (NoSuchMethodException e) {
                throw new NoZeroArgumentConstructorException();
            }

            /**
             * Reset accessibility to avoid missing future access exceptions.
             */
            field.setAccessible(accessibility); 
        }

        return thisJoinPoint.proceed();
    }   
}
