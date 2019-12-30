package com.github.everything.core.interceptor;

import com.github.everything.core.model.Thing;

@FunctionalInterface
public interface ThingInterceptor {

    void apply(Thing thing);
}
