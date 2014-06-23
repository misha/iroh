package com.misha.dedi.container.aspects;

import java.lang.reflect.Field;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.FieldSignature;

import com.misha.dedi.container.annotations.Autowired;
import com.misha.dedi.container.exceptions.DediException;
import com.misha.dedi.container.managers.InjectionManager;

@Aspect
public class AutowiringAspect {
        
    private final InjectionManager manager = new InjectionManager();

    private AutowiringAspect() throws DediException { }

    @Pointcut("execution((!com.misha.dedi.container..*).new(..))")
    public void construction() { }
    
    @Pointcut("cflow(within(!com.misha.dedi.container..*))")
    public void external() { }
                
    @Pointcut("get(@com.misha.dedi.container.annotations.Autowired * *) && @annotation(autowired)")
    public void access(Autowired autowired) { }

    @Before("construction() && this(target)")
    public void eagerlyInject(Object target) throws DediException {
        for (Field field : target.getClass().getDeclaredFields()) {
            manager.eagerlyInject(target, field);             
        }
    }

    @Before("access(autowired) && external()")
    public void lazilyInject(Autowired autowired, JoinPoint thisJoinPoint) 
        throws DediException {

        FieldSignature fs = (FieldSignature) thisJoinPoint.getSignature();
        Field field = fs.getField();
        Object target = thisJoinPoint.getTarget();
        manager.lazilyInject(target, field);
    }   
}
