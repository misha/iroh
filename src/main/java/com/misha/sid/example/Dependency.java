package com.misha.sid.example;

import com.misha.sid.annotations.Autowired;

public class Dependency {

    @Autowired
    private InternalDependency dependency;

    public InternalDependency getDependency() {
        return dependency;
    }

    public void setDependency(InternalDependency dependency) {
        this.dependency = dependency;
    }
}
