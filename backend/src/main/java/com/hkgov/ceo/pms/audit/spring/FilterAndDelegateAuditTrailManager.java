package com.hkgov.ceo.pms.audit.spring;

import com.hkgov.ceo.pms.audit.core.AuditActionContext;
import com.hkgov.ceo.pms.audit.core.AuditTrailManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * This is {@link FilterAndDelegateAuditTrailManager}.
 */
public class FilterAndDelegateAuditTrailManager implements AuditTrailManager, ApplicationEventPublisherAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(FilterAndDelegateAuditTrailManager.class);

    private final Collection<AuditTrailManager> auditTrailManagers;

    private final List<String> supportedActionsPerformed;

    private final List<String> excludedActionsPerformed;

    private ApplicationEventPublisher applicationEventPublisher;

    public FilterAndDelegateAuditTrailManager(final Collection<AuditTrailManager> auditTrailManagers,
                                              final List<String> supportedActionsPerformed,
                                              final List<String> excludedActionsPerformed) {
        this.auditTrailManagers = auditTrailManagers;
        this.supportedActionsPerformed = supportedActionsPerformed;
        this.excludedActionsPerformed = excludedActionsPerformed;
    }

    @Override
    public void setAuditFormat(final AuditFormats auditFormat) {
        auditTrailManagers.forEach(mgr -> mgr.setAuditFormat(auditFormat));
    }

    @Override
    public void record(final AuditActionContext auditActionContext) {
        var matched = supportedActionsPerformed
            .stream()
            .anyMatch(action -> {
                var actionPerformed = auditActionContext.getActionPerformed();
                return "*".equals(action) || Pattern.compile(action).matcher(actionPerformed).find();
            });

        if (matched) {
            matched = excludedActionsPerformed
                .stream()
                .noneMatch(action -> {
                    var actionPerformed = auditActionContext.getActionPerformed();
                    return "*".equals(action) || Pattern.compile(action).matcher(actionPerformed).find();
                });
        }
        if (matched) {
            LOGGER.trace("Recording audit action context [{}]", auditActionContext);
            auditTrailManagers.forEach(mgr -> mgr.record(auditActionContext));

            if (applicationEventPublisher != null) {
                var auditEvent = new AuditApplicationEvent(auditActionContext.getPrincipal(),
                    auditActionContext.getActionPerformed(), auditActionContext.getApplicationCode(),
                    auditActionContext.getClientIpAddress(), auditActionContext.getServerIpAddress(),
                    auditActionContext.getResourceOperatedUpon(), auditActionContext.getWhenActionWasPerformed().toString());
                applicationEventPublisher.publishEvent(auditEvent);
            }
        } else {
            LOGGER.trace("Skipping to record audit action context [{}] as it's not authorized as an audit action among [{}]",
                auditActionContext, supportedActionsPerformed);
        }
    }

    @Override
    public Set<? extends AuditActionContext> getAuditRecords(final Map<WhereClauseFields, Object> params) {
        return auditTrailManagers
            .stream()
            .map(mgr -> mgr.getAuditRecords(params))
            .flatMap(Set::stream)
            .collect(Collectors.toSet());
    }

    @Override
    public void removeAll() {
        auditTrailManagers.forEach(AuditTrailManager::removeAll);
    }

    @Override
    public void setApplicationEventPublisher(final ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}

