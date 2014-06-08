package com.misha.dedi.aspects;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.misha.dedi.annotations.Autowired;
import com.misha.dedi.annotations.Component;
import com.misha.dedi.exceptions.ComponentMethodInPrototypeException;
import com.misha.dedi.exceptions.DediException;
import com.misha.dedi.exceptions.NoSuchQualifierException;
import com.misha.dedi.exceptions.UnexpectedImplementationCountException;
import com.misha.dedi.exceptions.UnresolvableFieldException;
import com.misha.dedi.objects.ComponentSource;

@Aspect
public class AutowiringAspect {
        
    private final Multimap<Class<?>, ComponentSource> components = HashMultimap.create();
    
    private final Map<String, ComponentSource> qualified = new HashMap<>();
    
    private final Map<Class<?>, Object> instances = new HashMap<>();
                
    private final Reflections reflections = new Reflections("");
    
    private final Logger log = Logger.getLogger("dedi");
        
    private AutowiringAspect() throws DediException {
        for (Class<?> type : reflections.getTypesAnnotatedWith(Component.class)) {
            Component typeAnnotation = type.getAnnotation(Component.class);
            ComponentSource source = new ComponentSource(type, instances);
            components.put(source.getType(), source);
        
            if (!typeAnnotation.qualifier().equals("")) {
                qualified.put(typeAnnotation.qualifier(), source);
            }
            
            if (source.isConcrete()) {
                for (Method method : type.getMethods()) {
                    Component methodAnnotation = 
                        method.getAnnotation(Component.class);
                    
                    if (methodAnnotation != null) {
                        if (!methodAnnotation.scope().equals("singleton")) {
                            throw new ComponentMethodInPrototypeException(type);
                        }
                        
                        Object instance = null;
                        
                        if (instances.containsKey(type)) {
                            instance = instances.get(type);
                            
                        } else {
                            instance = source.getInstance();
                        }
                        
                        ComponentSource methodSource =
                            new ComponentSource(
                                instance, 
                                method, 
                                instances);
                        
                        components.put(methodSource.getType(), methodSource);
                        
                        if (!methodAnnotation.qualifier().equals("")) {
                            qualified.put(methodAnnotation.qualifier(), methodSource);
                        }
                    }
                }
            }
        }
    }
    
    @Pointcut("execution((! AutowiringAspect).new(..))")
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
            if (autowired != null && autowired.lazy() == false) {           
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
                ComponentSource source = 
                    resolveFromQualifier(field, annotation.qualifier());
                
                if (source == null) {
                    source = resolveDirectly(field);
                }
                
                if (source == null) {
                    source = resolveFromSubclasses(field);
                }
                
                if (source == null) {
                    throw new UnresolvableFieldException(field);
                }
   
                /**
                 * Inject a new instance for the field, checking for the prototype
                 * annotation as necessary. The default scoping policy is singleton.
                 */
                Object instance = source.getInstance();
                field.set(target, instance);
            }
            
        } catch (IllegalArgumentException | IllegalAccessException e) {
            
            /**
             * TODO: handle these exception elegantly.
             */
            throw new RuntimeException(e);
        
        } finally {
    
            /**
             * Reset accessibility to avoid missing future access exceptions.
             */
            field.setAccessible(accessibility); 
        }
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
}
