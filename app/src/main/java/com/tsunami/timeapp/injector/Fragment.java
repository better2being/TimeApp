package com.tsunami.timeapp.injector;

import java.lang.annotation.Retention;

import javax.inject.Scope;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author shenxiaoming
 */
@Scope
@Retention(RUNTIME)
public @interface Fragment {
}
