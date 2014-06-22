package com.misha.dedi.managers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Multimap;
import com.misha.dedi.exceptions.DediException;
import com.misha.dedi.exceptions.UnresolvableFieldException;
import com.misha.dedi.resolvers.DependencyResolver;
import com.misha.dedi.resolvers.DirectDependencyResolver;
import com.misha.dedi.resolvers.QualifiedDependencyResolver;
import com.misha.dedi.resolvers.SubclassDependencyResolver;
import com.misha.dedi.sources.Source;

public class DependencyResolverManager {

    @SuppressWarnings("serial")
    private List<DependencyResolver> resolvers = 
        new ArrayList<DependencyResolver>() {
            {
                add(new DirectDependencyResolver());
                add(new QualifiedDependencyResolver());
                add(new SubclassDependencyResolver());
            }
        };
    
    public DependencyResolverManager(Multimap<Class<?>, Source> sources) {
        for (DependencyResolver resolver : resolvers) {
            resolver.initialize(sources);
        }
    }

    public Source resolve(Field field) throws DediException {
        for (DependencyResolver resolver : resolvers) {
            Source source = resolver.resolve(field);
            
            if (source != null) {
                return source;
            }
        }
        
        throw new UnresolvableFieldException(field);
    }
}