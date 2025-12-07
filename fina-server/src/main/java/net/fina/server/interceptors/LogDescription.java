package net.fina.server.interceptors;


import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(METHOD)
@Retention(RUNTIME)
public @interface LogDescription {
    String name() default "";

    boolean ignore() default false;

    boolean logMethodParameters() default true;
}
