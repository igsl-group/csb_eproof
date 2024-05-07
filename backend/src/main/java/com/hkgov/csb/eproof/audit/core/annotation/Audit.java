package com.hkgov.csb.eproof.audit.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * States that this method should be logged for auditing purposes.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Audit {

    /**
     * Identifier for this particular application in the audit trail logs.  This attribute should only be used to override the basic application code when you want to differentiate a section of the code.
     *
     * @return the application code or an empty String if none is set.
     */
    String applicationCode() default "";

    /**
     * The action to write to the log when we audit this method.  Value must be defined.
     *
     * @return the action to write to the logs.
     */
    String action();

    /**
     * The resource may write to the log when we audit this method.  Value is optional.
     *
     * @return the resource or an empty String in none is set to the resource of logs.
     */
    String resourceWording() default "";

    /**
     * Reference name of the resource resolver to use.
     *
     * @return the reference to the resource resolver.  CANNOT be NULL.
     */
    String resourceResolverName();

    /**
     * Reference name of the action resolver to use.
     *
     * @return the reference to the action resolver.  CANNOT be NULL.
     */
    String actionResolverName() default "defaultAuditActionResolver";

    /**
     * Reference name of the principal resolver to use.
     *
     * @return the reference to the principal resolver.
     */
    String principalResolverName() default "";

    /**
     * Reference name of the user id resolver to use.
     *
     * @return the reference to the user id resolver.
     */
    String userIdResolverName() default "defaultAuditUserIdResolver";

    /**
     * Reference name of the status resolver to use.
     *
     * @return the reference to the status resolver. CANNOT be NULL.
     */
    String statusResolverName() default "defaultAuditStatusResolver";

    /**
     * Reference name of the request param resolver to use.
     *
     * @return the reference to the request param resolver. CANNOT be NULL.
     */
    String requestParamResolverName() default "defaultAuditRequestParamResolver";

    /**
     * Reference name of the request body resolver to use.
     *
     * @return the reference to the request body resolver. CANNOT be NULL.
     */
    String requestBodyResolverName() default "defaultAuditRequestBodyResolver";

    /**
     * Reference name of the return value  resolver to use.
     *
     * @return the reference to the return value resolver. CANNOT be NULL.
     */
    String retValResolverName() default "defaultAuditRetValResolver";

    /**
     * Reference name of the error message resolver to use.
     *
     * @return the reference to the error message resolver. CANNOT be NULL.
     */
    String errorMessageResolverName() default "defaultAuditErrorMessageResolver";
}
