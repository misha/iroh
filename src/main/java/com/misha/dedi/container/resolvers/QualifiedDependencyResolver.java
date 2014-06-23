package com.misha.dedi.container.resolvers;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.misha.dedi.container.annotations.Autowired;
import com.misha.dedi.container.exceptions.NoSuchQualifierException;
import com.misha.dedi.container.sources.Source;

public class QualifiedDependencyResolver implements DependencyResolver {

    private final Map<String, Source> qualified = new HashMap<>();
    
    public void register(Source source) {
        if (!source.getQualifier().equals("")) {
            qualified.put(source.getQualifier(), source);
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
