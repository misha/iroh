package com.misha.dedi.resolvers;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.Multimap;
import com.misha.dedi.annotations.Autowired;
import com.misha.dedi.exceptions.NoSuchQualifierException;
import com.misha.dedi.sources.Source;

public class QualifiedDependencyResolver implements DependencyResolver {

    private final Map<String, Source> qualified = new HashMap<>();
    
    public void initialize(Multimap<Class<?>, Source> sources) {
        for (Source source : sources.values()) {
            if (!source.getQualifier().equals("")) {
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
