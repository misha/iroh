package com.github.msoliter.iroh.container.resolvers;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.github.msoliter.iroh.container.annotations.Autowired;
import com.github.msoliter.iroh.container.exceptions.DuplicateQualifierException;
import com.github.msoliter.iroh.container.exceptions.NoSuchQualifierException;
import com.github.msoliter.iroh.container.sources.Source;

public class QualifiedDependencyResolver implements DependencyResolver {

    private final Map<String, Source> qualified = new ConcurrentHashMap<>();
    
    public void register(Source source) throws DuplicateQualifierException {
        String qualifier = source.getQualifier();
        
        if (!qualifier.equals("")) {
            if (qualified.containsKey(qualifier)) {
                throw new DuplicateQualifierException(qualifier);
            
            } else {
                qualified.put(source.getQualifier(), source);
            }
        }
    }
    
    @Override
    public Source resolve(Field field) throws NoSuchQualifierException {      
        Autowired autowired = field.getAnnotation(Autowired.class);
        String qualifier = autowired.qualifier();
        
        if (!qualifier.equals("")) {
            Source source = qualified.get(qualifier);

            if (source == null) {
                throw new NoSuchQualifierException(qualifier);
            }
            
            return source;
        }
        
        return null;
    }
}
