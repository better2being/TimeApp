package com.tsunami.timeapp.injector;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import javax.inject.Qualifier;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author shenxiaoming
 */
@Qualifier
@Documented
@Retention(RUNTIME)
public @interface ContextLifeCycle {
    String value() default "App";
}
