package com.misha.dedi.container.aspects;

import java.lang.reflect.Field;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.FieldSignature;
import org.reflections.Reflections;

import com.misha.dedi.container.annotations.Component;
import com.misha.dedi.container.exceptions.DediException;
import com.misha.dedi.container.managers.InjectionManager;
import com.misha.dedi.container.managers.ResolutionManager;
import com.misha.dedi.container.resolvers.DirectDependencyResolver;
import com.misha.dedi.container.resolvers.QualifiedDependencyResolver;
import com.misha.dedi.container.resolvers.SubclassDependencyResolver;

@Aspect
public class AutowiringAspect {
        
    private final ResolutionManager resolver = new ResolutionManager(
        new DirectDependencyResolver(),
        new QualifiedDependencyResolver(),
        new SubclassDependencyResolver());
    
    private final InjectionManager injector = new InjectionManager(resolver);

    private AutowiringAspect() throws DediException { 
        
        // Hopefully, this gets garbage collected...
        Reflections reflections = new Reflections("");
        
        for (Class<?> type : reflections.getTypesAnnotatedWith(Component.class)) {
            resolver.register(type);
        }
    }

    @Pointcut("execution(*.new(..))")
    public void construction() { }
    
    @Pointcut("cflow(within(!com.misha.dedi.container..*))")
    public void external() { }

    @Pointcut("get(@com.misha.dedi.container.annotations.Autowired * *)")
    public void access() { }
    
    @Before("external() && construction() && this(target)")
    public void eagerlyInjectObject(Object target) throws DediException {        
        for (Field field : target.getClass().getDeclaredFields()) {
            injector.eagerlyInject(target, field);             
        }
    }

    @Before("external() && access()")
    public void lazilyInjectField(JoinPoint thisJoinPoint) 
        throws DediException {

        FieldSignature fs = (FieldSignature) thisJoinPoint.getSignature();
        Field field = fs.getField();
        Object target = thisJoinPoint.getTarget();
        injector.lazilyInject(target, field);
    }   
}
