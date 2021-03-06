package org.fenixedu.bennu.portal;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ TYPE, METHOD })
public @interface Functionality {
    Class<?> app();

    String path();

    String bundle();

    String title();

    String description();

    String group() default "anyone";
}