package com.hkgov.csb.eproof.audit.core;

import com.hkgov.csb.eproof.audit.common.spi.ClientInfoResolver;
import com.hkgov.csb.eproof.audit.common.spi.DefaultClientInfoResolver;
import com.hkgov.csb.eproof.audit.common.spi.PrincipalResolver;
import com.hkgov.csb.eproof.audit.core.annotation.Audit;
import com.hkgov.csb.eproof.audit.core.annotation.Audits;
import com.hkgov.csb.eproof.audit.core.spi.AuditActionResolver;
import com.hkgov.csb.eproof.audit.core.spi.AuditErrorMessageResolver;
import com.hkgov.csb.eproof.audit.core.spi.AuditRequestBodyResolver;
import com.hkgov.csb.eproof.audit.core.spi.AuditRequestParamResolver;
import com.hkgov.csb.eproof.audit.core.spi.AuditResourceResolver;
import com.hkgov.csb.eproof.audit.core.spi.AuditRetValResolver;
import com.hkgov.csb.eproof.audit.core.spi.AuditStatusResolver;
import com.hkgov.csb.eproof.audit.core.spi.AuditUserIdResolver;
import com.hkgov.csb.eproof.audit.core.spi.support.BooleanAuditActionResolver;
import com.hkgov.csb.eproof.audit.core.spi.support.DefaultAuditActionResolver;
import com.hkgov.csb.eproof.audit.core.spi.support.ObjectCreationAuditActionResolver;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * A POJO style aspect modularizing management of an audit trail data concern.
 */
@Aspect
@Component
public class AuditTrailManagementAspect {

    private static final Logger LOG = LoggerFactory.getLogger(AuditTrailManagementAspect.class);
    private final PrincipalResolver defaultAuditPrincipalResolver;
    private final Map<String, AuditActionResolver> auditActionResolvers;
    private final Map<String, AuditResourceResolver> auditResourceResolvers;
    private final Map<String, PrincipalResolver> auditPrincipalResolvers;
    private final Map<String, AuditUserIdResolver> auditUserIdResolvers;
    private final Map<String, AuditStatusResolver> auditStatusResolvers;
    private final Map<String, AuditRequestParamResolver> auditRequestParamResolvers;
    private final Map<String, AuditRequestBodyResolver> auditRequestBodyResolvers;
    private final Map<String, AuditRetValResolver> auditRetValResolvers;
    private final Map<String, AuditErrorMessageResolver> auditErrorMessageResolvers;
    private final List<AuditTrailManager> auditTrailManagers;
    private final String applicationCode;

    protected AuditTrailManager.AuditFormats auditFormat = AuditTrailManager.AuditFormats.DEFAULT;

    private ClientInfoResolver clientInfoResolver = new DefaultClientInfoResolver();

    private boolean failOnAuditFailures = true;

    private boolean enabled = true;

    /**
     * Constructs an AuditTrailManagementAspect with the following parameters.  Also, registers
     * some default AuditActionResolvers including the
     * {@link DefaultAuditActionResolver}, the {@link BooleanAuditActionResolver}
     * and the {@link ObjectCreationAuditActionResolver}.
     *
     * @param defaultAuditPrincipalResolver the default audit principal resolver
     * @param auditActionResolvers          the map of resolvers by name provided in the annotation on the method.
     * @param auditResourceResolvers        the map of resolvers by the name provided in the annotation on the method.
     * @param auditPrincipalResolvers       the map of resolvers by name provided in the annotation on the method.
     * @param auditTrailManagers            the list of managers to write the audit trail out to.
     * @param applicationCode               the overall code that identifies this application.
     */
    public AuditTrailManagementAspect(PrincipalResolver defaultAuditPrincipalResolver,
                                      Map<String, AuditActionResolver> auditActionResolvers,
                                      Map<String, AuditResourceResolver> auditResourceResolvers,
                                      Map<String, PrincipalResolver> auditPrincipalResolvers,
                                      Map<String, AuditUserIdResolver> auditUserIdResolvers,
                                      Map<String, AuditStatusResolver> auditStatusResolvers,
                                      Map<String, AuditRequestParamResolver> auditRequestParamResolvers,
                                      Map<String, AuditRequestBodyResolver> auditRequestBodyResolvers,
                                      Map<String, AuditRetValResolver> auditRetValResolvers,
                                      Map<String, AuditErrorMessageResolver> auditErrorMessageResolvers,
                                      List<AuditTrailManager> auditTrailManagers,
                                      String applicationCode) {
        this.defaultAuditPrincipalResolver = defaultAuditPrincipalResolver;
        this.auditActionResolvers = auditActionResolvers;
        this.auditResourceResolvers = auditResourceResolvers;
        this.auditPrincipalResolvers = auditPrincipalResolvers;
        this.auditUserIdResolvers = auditUserIdResolvers;
        this.auditStatusResolvers = auditStatusResolvers;
        this.auditRequestParamResolvers = auditRequestParamResolvers;
        this.auditRequestBodyResolvers = auditRequestBodyResolvers;
        this.auditRetValResolvers = auditRetValResolvers;
        this.auditErrorMessageResolvers = auditErrorMessageResolvers;
        this.auditTrailManagers = auditTrailManagers;
        this.applicationCode = applicationCode;
    }

    @Around(value = "@annotation(audits)", argNames = "audits")
    public Object handleAuditTrail(final ProceedingJoinPoint joinPoint, final Audits audits) throws Throwable {
        if (!this.enabled) {
            return joinPoint.proceed();
        }

        Object retVal = null;
        String currentPrincipal = null;
        String status = null;
        Exception ex = null;
        final var actions = new String[audits.value().length];
        final var auditableResources = new String[audits.value().length][];
        try {
            retVal = joinPoint.proceed();

            currentPrincipal = getCurrentPrincipal(joinPoint, audits, retVal);

            if (currentPrincipal != null) {
                for (var i = 0; i < audits.value().length; i++) {
                    var audit = audits.value()[i];
                    final var auditActionResolver = auditActionResolvers.get(audit.actionResolverName());
                    final var auditStatusResolver = auditStatusResolvers.get(audit.statusResolverName());
                    final var auditResourceResolver = auditResourceResolvers.get(audit.resourceResolverName());
                    auditResourceResolver.setAuditFormat(this.auditFormat);

                    auditableResources[i] = auditResourceResolver.resolveFrom(joinPoint, retVal, audit);
                    actions[i] = auditActionResolver.resolveFrom(joinPoint, retVal, audit);
                    status = auditStatusResolver.getSuccessStatus();
                }
            }
            return retVal;
        } catch (final Throwable t) {
            final var e = wrapIfNecessary(t);
            ex = e;
            currentPrincipal = getCurrentPrincipal(joinPoint, audits, e);

            if (currentPrincipal != null) {
                for (var i = 0; i < audits.value().length; i++) {
                    var audit = audits.value()[i];
                    var auditResourceResolver = this.auditResourceResolvers.get(audit.resourceResolverName());
                    auditResourceResolver.setAuditFormat(this.auditFormat);

                    auditableResources[i] = auditResourceResolver.resolveFrom(joinPoint, audit, e);
                    actions[i] = auditActionResolvers.get(audit.actionResolverName()).resolveFrom(joinPoint, e, audit);
                    status = auditStatusResolvers.get(audit.statusResolverName()).getFailureStatus();
                }
            }
            throw t;
        } finally {
            for (var i = 0; i < audits.value().length; i++) {
                executeAuditCode(currentPrincipal, auditableResources[i], joinPoint, retVal, actions[i], audits.value()[i], status, ex);
            }
        }
    }

    @Around(value = "@annotation(audit)", argNames = "audit")
    public Object handleAuditTrail(final ProceedingJoinPoint joinPoint, final Audit audit) throws Throwable {
        if (!this.enabled) {
            return joinPoint.proceed();
        }

        final var auditActionResolver = this.auditActionResolvers.get(audit.actionResolverName());
        final var auditResourceResolver = this.auditResourceResolvers.get(audit.resourceResolverName());
        final var auditStatusResolver = this.auditStatusResolvers.get(audit.statusResolverName());
        auditResourceResolver.setAuditFormat(this.auditFormat);

        String currentPrincipal = null;
        var auditResource = new String[]{null};
        String action = null;
        Object retVal = null;
        String status = null;
        Exception ex = null;
        try {
            retVal = joinPoint.proceed();

            currentPrincipal = getCurrentPrincipal(joinPoint, audit, retVal);

            auditResource = auditResourceResolver.resolveFrom(joinPoint, retVal, audit);
            action = auditActionResolver.resolveFrom(joinPoint, retVal, audit);
            status = auditStatusResolver.getSuccessStatus();
            return retVal;
        } catch (final Throwable t) {
            final var e = wrapIfNecessary(t);
            ex = e;
            currentPrincipal = getCurrentPrincipal(joinPoint, audit, e);
            auditResource = auditResourceResolver.resolveFrom(joinPoint, audit, e);
            action = auditActionResolver.resolveFrom(joinPoint, e, audit);
            status = auditStatusResolver.getFailureStatus();
            throw t;
        } finally {
            executeAuditCode(currentPrincipal, auditResource, joinPoint, retVal, action, audit, status, ex);
        }
    }

    public void setFailOnAuditFailures(final boolean failOnAuditFailures) {
        this.failOnAuditFailures = failOnAuditFailures;
    }

    public void setClientInfoResolver(final ClientInfoResolver factory) {
        this.clientInfoResolver = factory;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    private String getCurrentPrincipal(final ProceedingJoinPoint joinPoint, final Audits audits, final Object retVal) {
        String currentPrincipal = null;
        for (var i = 0; i < audits.value().length; i++) {
            var resolverName = audits.value()[i].principalResolverName();
            if (resolverName.trim().length() > 0) {
                final var resolver = this.auditPrincipalResolvers.get(resolverName);
                currentPrincipal = resolver.resolveFrom(joinPoint, retVal);
            }
        }

        if (currentPrincipal == null) {
            currentPrincipal = this.defaultAuditPrincipalResolver.resolveFrom(joinPoint, retVal);
        }
        return currentPrincipal;
    }

    private String getCurrentPrincipal(final ProceedingJoinPoint joinPoint, final Audit audit, final Object retVal) {
        String currentPrincipal = null;
        var resolverName = audit.principalResolverName();
        if (resolverName.trim().length() > 0) {
            final var resolver = this.auditPrincipalResolvers.get(resolverName);
            currentPrincipal = resolver.resolveFrom(joinPoint, retVal);
        }
        if (currentPrincipal == null) {
            currentPrincipal = this.defaultAuditPrincipalResolver.resolveFrom(joinPoint, retVal);
        }
        return currentPrincipal;
    }

    private void executeAuditCode(final String currentPrincipal, final String[] auditableResources,
                                  final ProceedingJoinPoint joinPoint, final Object retVal,
                                  final String action, final Audit audit, final String status, final Exception ex) {
        final var applicationCode = (audit.applicationCode() != null
                && audit.applicationCode().length() > 0) ? audit.applicationCode() : this.applicationCode;
        final var clientInfo = clientInfoResolver.resolveFrom(joinPoint, retVal);
        final var userId = auditUserIdResolvers.get(audit.userIdResolverName()).resolveFrom(currentPrincipal);
        final var actionDate = LocalDateTime.now();
        final var requestParam = auditRequestParamResolvers.get(audit.requestParamResolverName()).resolveFrom(clientInfo.getRequest());
        final var requestBody = auditRequestBodyResolvers.get(audit.requestBodyResolverName()).resolveFrom(clientInfo.getRequest());
        final String returnValue = auditRetValResolvers.get(audit.retValResolverName()).resolveFrom(retVal);
        final String errorMessage = auditErrorMessageResolvers.get(audit.errorMessageResolverName()).resolveFrom(ex);
        final AuditPointRuntimeInfo runtimeInfo = new AspectJAuditPointRuntimeInfo(joinPoint);

        assertNotNull(currentPrincipal, "'principal' cannot be null.\n" + getDiagnosticInfo(runtimeInfo));
        assertNotNull(action, "'actionPerformed' cannot be null.\n" + getDiagnosticInfo(runtimeInfo));
        assertNotNull(applicationCode, "'applicationCode' cannot be null.\n" + getDiagnosticInfo(runtimeInfo));
        assertNotNull(actionDate, "'whenActionPerformed' cannot be null.\n" + getDiagnosticInfo(runtimeInfo));
        assertNotNull(clientInfo.getClientIpAddress(), "'clientIpAddress' cannot be null.\n" + getDiagnosticInfo(runtimeInfo));
        assertNotNull(clientInfo.getServerIpAddress(), "'serverIpAddress' cannot be null.\n" + getDiagnosticInfo(runtimeInfo));

        for (final var auditableResource : auditableResources) {
            assertNotNull(auditableResource, "'resourceOperatedUpon' cannot be null.\n" + getDiagnosticInfo(runtimeInfo));
            final var auditContext =
                    new AuditActionContext(
                            userId,
                            currentPrincipal,
                            auditableResource,
                            action,
                            status,
                            applicationCode,
                            actionDate,
                            clientInfo.getClientIpAddress(),
                            clientInfo.getServerIpAddress(),
                            clientInfo.getUserAgent(),
                            clientInfo.getRequestUrl(),
                            clientInfo.getRequestMethod(),
                            requestParam,
                            requestBody,
                            returnValue,
                            errorMessage
                    );

            try {
                for (final var manager : auditTrailManagers) {
                    manager.setAuditFormat(this.auditFormat);
                    manager.record(auditContext);
                }
            } catch (final Throwable e) {
                if (this.failOnAuditFailures) {
                    throw e;
                }
                LOG.error("Failed to record audit context for "
                        + auditContext.getActionPerformed()
                        + " and principal " + auditContext.getPrincipal(), e);
            }
        }
    }

    private String getDiagnosticInfo(AuditPointRuntimeInfo runtimeInfo) {
        return "Check the correctness of @Audit annotation at the following audit point: " + runtimeInfo.asString();
    }

    private void assertNotNull(final Object o, final String message) {
        if (o == null) {
            throw new IllegalArgumentException(message);
        }
    }

    private Exception wrapIfNecessary(final Throwable t) {
        return t instanceof Exception ex ? ex : new Exception(t);
    }
}
