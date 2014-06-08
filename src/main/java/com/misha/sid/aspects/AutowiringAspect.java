package com.misha.sid.aspects;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.FieldSignature;

import com.misha.sid.annotations.Autowired;

@Aspect
public class AutowiringAspect {
    
    private Map<Class<?>, Object> instances = new HashMap<Class<?>, Object>();
        
    @Pointcut(
        "get(@com.misha.sid.annotations.Autowired * *) && " +
        "@annotation(annotation)")
    public void autowired(Autowired annotation) { 
        
    }

    @Around("autowired(annotation)")
    public Object resolve(
        Autowired annotation, 
        ProceedingJoinPoint thisJoinPoint) 
        throws Throwable {
        
        /**
         * Figure out which field is supposed to be autowired.
         */
        FieldSignature fs = (FieldSignature) thisJoinPoint.getSignature();
        Field field = fs.getField();
        
        /**
         * Grab a pointer to the actual field instance and its current value.
         */
        boolean accessibility = field.isAccessible();
        field.setAccessible(true);
        Object target = thisJoinPoint.getTarget();
        Object currentValue = field.get(target);
        
        /**
         * If it hasn't been autowired yet, fill it in with an instance  based
         * on the scoping policy designated by the annotation.
         */
        if (currentValue == null) {
            Class<?> type = field.getType();

            /**
             * If it's a singleton, get the cached instance, or create a new
             * instance and cache it for future field accesses.
             */
            if (annotation.value().equals("singleton")) {
                if (!instances.containsKey(type)) {
                    instances.put(type, type.getConstructor().newInstance());
                }
                
                field.set(target, instances.get(type));

            } else {
                
                /**
                 * Otherwise, just create a new instance every time.
                 */
                field.set(target, type.getConstructor().newInstance());
            }
            
            /**
             * Reset the field's accessibility.
             */
            field.setAccessible(accessibility);
        }
        
        /**
         * Propagate the function call.
         */
        return thisJoinPoint.proceed();
    }   
}
