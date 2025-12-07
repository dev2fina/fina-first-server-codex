package net.fina.first.interceptors;


import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(METHOD)
@Retention(RUNTIME)
public @interface FirstLogDescription {
    String name() default "UNNAMED METHOD (Please fix me).";

    boolean ignore() default false;
}
