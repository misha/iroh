package com.misha.dedi.aspects;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.FieldSignature;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import com.misha.dedi.annotations.Autowired;
import com.misha.dedi.exceptions.DediException;
import com.misha.dedi.managers.SourceManager;

@Aspect
public class AutowiringAspect {
                                    
    private final Map<Class<?>, Object> nulls = new HashMap<>();
    
    private final SourceManager manager = new SourceManager();
    
    private final Objenesis objenesis = new ObjenesisStd(true);
        
    private final static Logger log = Logger.getLogger("dedi");
            
    private final static Level level = Level.ALL;
    
    static {
        log.setLevel(level);
    }
    
    private AutowiringAspect() throws DediException {     
        // checkForCycles();
    }
    
//    @SuppressWarnings("serial")
//    private void checkForCycles() throws DediException {
//        for (ComponentSourceImpl source : components.values()) {
//            final Class<?> clazz = source.getType();           
//            checkForCycles(new HashSet<Class<?>>() {{ add(clazz); }}, clazz);
//        }
//    }
//    
//    private void checkForCycles(Set<Class<?>> existing, Class<?> target) 
//        throws DependencyCycleException {
//        
//        for (Field field : getAutowirableFields(target)) {
//            if (existing.contains(field.getClass())) {
//                throw new DependencyCycleException(field, target);
//                
//            } else {
//                existing.add(field.getClass());
//                checkForCycles(existing, field.getClass());
//            }
//        }
//    }

    @Pointcut("execution((!com.misha.dedi..* || com.misha.dedi.tests..*).new(..))")
    public void onConstruction() { }
                
    @Pointcut("get(@com.misha.dedi.annotations.Autowired * *) && @annotation(annotation)")
    public void onAutowiredFieldAccess(Autowired annotation) { }

    @Before("onConstruction() && this(target)")
    public void eagerlyInject(Object target) throws DediException {
        for (Field field : getAutowirableFields(target.getClass())) {
            Autowired autowired = field.getAnnotation(Autowired.class);
            
            if (autowired.lazy() == false) {           
                log.info(String.format(
                    "eagerly injecting field '%s' in '%s'",
                    field.getName(),
                    target.toString()));
                
                inject(autowired, field, target);   
                
            } else {
                nullify(field, autowired, target);
            }    
        }
    }

    @Before("onAutowiredFieldAccess(annotation)")
    public void lazilyInject(Autowired annotation, JoinPoint thisJoinPoint) 
        throws DediException {

        FieldSignature fs = (FieldSignature) thisJoinPoint.getSignature();
        Field field = fs.getField();
        field.setAccessible(true);
        Object target = thisJoinPoint.getTarget();
        
        if (isNullified(field, target)) {
            log.info(String.format(
                "lazily injecting field '%s' in '%s'",
                field.getName(),
                target.toString()));

            inject(annotation, field, target);            
        }
    }   
    
    private void inject(Autowired annotation, Field field, Object target) 
        throws DediException {

        try {
            field.set(target, manager.getInstance(field));
        
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(e);        
        }
    }
    
    private Set<Field> getAutowirableFields(Class<?> clazz) {
        Set<Field> fields = new HashSet<>();
        
        for (Field field : clazz.getDeclaredFields()) {
            Autowired autowired = field.getAnnotation(Autowired.class);

            if (autowired != null) { 
                field.setAccessible(true);
                fields.add(field);
            }
        }
        
        return fields;
    }
    
    private boolean isNullified(Field field, Object target) {
        Class<?> type = field.getType();
        Object value = null;
        
        try {
            value = field.get(target);
            
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        
        if (nulls.containsKey(type)) {
            if (nulls.get(type) == value) {
                return true;
            }
        }
        
        return false;
    }
    
    private void nullify(Field field, Autowired annotation, Object target) 
        throws DediException {       
        
        Class<?> type = field.getType();
        
        if (!nulls.containsKey(type)) {
            nulls.put(type, objenesis.newInstance(manager.getImplementingType(field)));
        }
        
        try {
            Object local = nulls.get(type);
            log.info(String.format(
                "nullifying field '%s' with '%s' in '%s'", 
                field.getName(), 
                local.toString(),
                target.toString()));
            
            field.set(target, local);
            
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(e);        
        }
    }
}
