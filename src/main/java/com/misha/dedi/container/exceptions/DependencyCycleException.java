package com.misha.dedi.container.exceptions;

import java.util.List;

import com.google.common.base.Joiner;

@SuppressWarnings("serial")
public class DependencyCycleException extends DediException {

    public DependencyCycleException(final List<Class<?>> trace) {
        super(
            String.format(
                "A dependency cycle was detected: %s.",
                Joiner
                    .on(" => ")
                    .join(trace)));
    }
}
