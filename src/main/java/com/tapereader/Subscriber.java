package com.tapereader;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

@Retention(RUNTIME)
public @interface Subscriber {

    String className();

    String methodName();

}