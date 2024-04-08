package com.hkgov.ceo.pms.audit.core.util;

import org.aspectj.lang.JoinPoint;


/**
 * Utility class to assist with AOP operations.
 */
public class AopUtils {

    /**
     * Instantiates a new aop utils.
     */
    private AopUtils() {}

    /**
     * Unwraps a join point that may be nested due to layered proxies.
     *
     * @param point Join point to unwrap.
     * @return Innermost join point; if not nested, simply returns the argument.
     */
    public static JoinPoint unWrapJoinPoint(final JoinPoint point) {
        var naked = point;
        while (naked.getArgs().length > 0 && naked.getArgs()[0] instanceof JoinPoint) {
            naked = (JoinPoint) naked.getArgs()[0];
        }
        return naked;
    }
}
