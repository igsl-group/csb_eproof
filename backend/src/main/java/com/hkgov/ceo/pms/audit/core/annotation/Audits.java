package com.hkgov.ceo.pms.audit.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * States that this method should be logged for auditing purposes.
 * <p>
 * This is a holder annotation to hold multiple {@link Audit} annotations.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Audits {

    Audit[] value();
}
