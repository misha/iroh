package com.misha.dedi.aspects;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
import org.reflections.Reflections;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.misha.dedi.annotations.Autowired;
import com.misha.dedi.annotations.Component;
import com.misha.dedi.exceptions.ComponentMethodInPrototypeException;
import com.misha.dedi.exceptions.DediException;
import com.misha.dedi.exceptions.NoSuchQualifierException;
import com.misha.dedi.exceptions.NonConcreteComponentClassException;
import com.misha.dedi.exceptions.UnexpectedImplementationCountException;
import com.misha.dedi.objects.ComponentSource;

@Aspect
public class AutowiringAspect {
        
    private final Multimap<Class<?>, ComponentSource> components = HashMultimap.create();
    
    private final Map<String, ComponentSource> qualified = new HashMap<>();
    
    private final Map<Class<?>, Object> instances = new HashMap<>();
                    
    private final Map<Class<?>, Object> nulls = new HashMap<>();
    
    private final Reflections reflections = new Reflections("");
    
    private final Objenesis objenesis = new ObjenesisStd(true);
    
    private final static Logger log = Logger.getLogger("dedi");
            
    private final static Level level = Level.OFF;
    
    static {
        log.setLevel(level);
    }
    
    private AutowiringAspect() throws DediException {     
        for (Class<?> type : reflections.getTypesAnnotatedWith(Component.class)) {
            initializeType(type);
        }
    }
    
    private void initializeType(Class<?> type) throws DediException {
        Component annotation = type.getAnnotation(Component.class);
        ComponentSource source = new ComponentSource(type, instances);
        register(source, annotation.qualifier());
        initializeMethods(source);
    }
    
    private void initializeMethods(ComponentSource source) throws DediException {
        Class<?> type = source.getType();
        
        for (Method method : type.getMethods()) {
            Component annotation = method.getAnnotation(Component.class);
            
            if (annotation != null) {
                if (!annotation.scope().equals("singleton")) {
                    throw new ComponentMethodInPrototypeException(type);
                }

                register(
                    new ComponentSource(
                        source, 
                        method, 
                        instances),
                    annotation.qualifier());
            }
        }
    }
    
    private void register(ComponentSource source, String qualifier) 
        throws NonConcreteComponentClassException {

        if (!source.isConcrete()) {
            throw new NonConcreteComponentClassException(source.getType());
        }

        Class<?> type = source.getType();
        log.info("registering " + type);
        components.put(type, source);
        
        if (!qualifier.equals("")) {
            qualified.put(qualifier, source);
        }
    }
    
    @Pointcut("execution((@com.misha.dedi.annotations.Component *).new(..))")
    public void onConstruction() { }
                
    @Pointcut("get(@com.misha.dedi.annotations.Autowired * *) && @annotation(annotation)")
    public void onFieldAccess(Autowired annotation) { }
     
    /**
     * Injects dependencies into an object prior to the constructor.
     */
    @Before("onConstruction() && this(target)")
    public void eagerlyInject(Object target) throws DediException {
        for (Field field : target.getClass().getDeclaredFields()) {
            Autowired autowired = field.getAnnotation(Autowired.class);

            if (autowired != null) { 
                field.setAccessible(true);
                
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
    }

    /**
     * Injects dependencies into a field prior to accessing the field.
     */
    @Before("onFieldAccess(annotation) && !onConstruction()")
    public void lazilyInject(Autowired annotation, JoinPoint thisJoinPoint) 
        throws DediException {

        /**
         * Figure out which field needs to get injected. If we allow the
         * autowired annotation on things other than fields, then this
         * cast will have to be refactored into a lookup.
         */
        FieldSignature fs = (FieldSignature) thisJoinPoint.getSignature();
        Field field = fs.getField();
        field.setAccessible(true);

        /**
         * Get the target field instance, and the type of object that needs
         * to be injected into the field instance.
         */
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
            field.set(target, resolve(field, annotation).getInstance());
        
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(e);        
        }
    }
    
    private ComponentSource resolve(Field field, Autowired annotation) 
        throws DediException {
        
        ComponentSource source = 
            resolveFromQualifier(field, annotation.qualifier());
        
        if (source == null) {
            source = resolveDirectly(field);
        }
        
        if (source == null) {
            source = resolveFromSubclasses(field);
        }
        
        return source;
    }
    
    private ComponentSource resolveDirectly(Field field) {
        Collection<ComponentSource> potential = components.get(field.getType());
        Iterator<ComponentSource> iterator = potential.iterator();
        
        while (iterator.hasNext()) {
            ComponentSource candidate = iterator.next();
            
            if (!candidate.isConcrete()) {
                iterator.remove();
            }
        }
        
        if (potential.size() == 1) {
            ComponentSource source = potential.iterator().next();
            
            if (source.isConcrete()) {
                return source;
            }
        }
        
        return null;
    }
    
    private ComponentSource resolveFromQualifier(Field field, String qualifier) 
        throws NoSuchQualifierException {
        
        if (!qualifier.equals("")) {
            ComponentSource source = qualified.get(qualifier);
            
            /**
             * Validate that we got an actual type from the qualified list.
             */
            if (source == null) {
                throw new NoSuchQualifierException(qualifier);
            }
            
            return source;
        }
        
        return null;
    }
    
    private ComponentSource resolveFromSubclasses(Field field) 
        throws UnexpectedImplementationCountException {
        
        Set<ComponentSource> potential = new HashSet<>();
        Class<?> target = field.getType();
        
        for (Class<?> type : components.keySet()) {
            if (target.isAssignableFrom(type)) {
                for (ComponentSource source : components.get(type)) {
                    if (source.isConcrete()) {
                        potential.add(source);
                    }
                }
            }
        }

        if (potential.size() != 1) {
            throw new UnexpectedImplementationCountException(target, potential.size());
        
        } else {
            return potential.iterator().next();
        }
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
            nulls.put(type, objenesis.newInstance(resolve(field, annotation).getType()));
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
