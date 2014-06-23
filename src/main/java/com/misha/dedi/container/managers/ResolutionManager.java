package com.misha.dedi.container.managers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Multimap;
import com.misha.dedi.container.exceptions.DediException;
import com.misha.dedi.container.exceptions.UnresolvableFieldException;
import com.misha.dedi.container.resolvers.DependencyResolver;
import com.misha.dedi.container.resolvers.DirectDependencyResolver;
import com.misha.dedi.container.resolvers.QualifiedDependencyResolver;
import com.misha.dedi.container.resolvers.SubclassDependencyResolver;
import com.misha.dedi.container.sources.Source;

public class ResolutionManager {

    @SuppressWarnings("serial")
    private List<DependencyResolver> resolvers = 
        new ArrayList<DependencyResolver>() {
            {
                add(new DirectDependencyResolver());
                add(new QualifiedDependencyResolver());
                add(new SubclassDependencyResolver());
            }
        };
    
    public ResolutionManager(Multimap<Class<?>, Source> sources) {
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
