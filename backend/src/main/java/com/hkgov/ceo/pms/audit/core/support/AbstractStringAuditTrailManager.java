package com.hkgov.ceo.pms.audit.core.support;

import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;
import com.hkgov.ceo.pms.audit.core.AuditActionContext;
import com.hkgov.ceo.pms.audit.core.AuditTrailManager;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Abstract AuditTrailManager that turns the AuditActionContext into a printable String.
 */
public abstract class AbstractStringAuditTrailManager implements AuditTrailManager {
    /**
     * what format should the audit log entry use?
     */
    private AuditFormats auditFormat = AuditFormats.DEFAULT;

    /**
     * Use multi-line output by default
     **/
    private boolean useSingleLine = false;

    /**
     * Separator for single line log entries
     */
    private String entrySeparator = ",";

    public void setUseSingleLine(final boolean useSingleLine) {
        this.useSingleLine = useSingleLine;
    }

    @Override
    public void setAuditFormat(final AuditTrailManager.AuditFormats auditFormat) {
        this.auditFormat = auditFormat;
    }

    @Override
    public Set<? extends AuditActionContext> getAuditRecords(final Map<WhereClauseFields, Object> whereClause) {
        return new HashSet<>();
    }

    protected String getEntrySeparator() {
        return this.entrySeparator;
    }

    public void setEntrySeparator(final String separator) {
        this.entrySeparator = separator;
    }

    protected String toString(final AuditActionContext auditActionContext) {
        if (auditFormat == AuditFormats.JSON) {
            final var builder = new StringBuilder();

            try {
                if (this.useSingleLine) {
                    final var writer = AuditTrailManager.MAPPER.writer(new MinimalPrettyPrinter());
                    builder.append(writer.writeValueAsString(getJsonObjectForAudit(auditActionContext)));
                } else {
                    builder.append(AuditTrailManager.MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(getJsonObjectForAudit(auditActionContext)));
                    builder.append("\n");
                }
            } catch (final Exception e) {
                throw new IllegalArgumentException(e.getMessage(), e);
            }
            return builder.toString();
        }

        if (this.useSingleLine) {
            return getSingleLineAuditString(auditActionContext);
        }
        return getMultiLineAuditString(auditActionContext);
    }

    protected String getMultiLineAuditString(final AuditActionContext auditActionContext) {
        final var builder = new StringBuilder();
        builder.append("Audit trail record BEGIN\n");
        builder.append("=============================================================\n");
        builder.append("WHO: ");
        builder.append(auditActionContext.getPrincipal());
        builder.append("\n");
        builder.append("WHAT: ");
        builder.append(auditActionContext.getResourceOperatedUpon());
        builder.append("\n");
        builder.append("ACTION: ");
        builder.append(auditActionContext.getActionPerformed());
        builder.append("\n");
        builder.append("APPLICATION: ");
        builder.append(auditActionContext.getApplicationCode());
        builder.append("\n");
        builder.append("WHEN: ");
        builder.append(auditActionContext.getWhenActionWasPerformed());
        builder.append("\n");
        builder.append("CLIENT IP ADDRESS: ");
        builder.append(auditActionContext.getClientIpAddress());
        builder.append("\n");
        builder.append("SERVER IP ADDRESS: ");
        builder.append(auditActionContext.getServerIpAddress());
        builder.append("\n");
        builder.append("=============================================================");
        builder.append("\n\n");

        return builder.toString();
    }

    protected String getSingleLineAuditString(final AuditActionContext auditActionContext) {
        final var builder = new StringBuilder();
        builder.append(auditActionContext.getWhenActionWasPerformed());
        builder.append(getEntrySeparator());
        builder.append(auditActionContext.getApplicationCode());
        builder.append(getEntrySeparator());
        builder.append(auditActionContext.getResourceOperatedUpon());
        builder.append(getEntrySeparator());
        builder.append(auditActionContext.getActionPerformed());
        builder.append(getEntrySeparator());
        builder.append(auditActionContext.getPrincipal());
        builder.append(getEntrySeparator());
        builder.append(auditActionContext.getClientIpAddress());
        builder.append(getEntrySeparator());
        builder.append(auditActionContext.getServerIpAddress());

        return builder.toString();
    }

    protected Map getJsonObjectForAudit(final AuditActionContext auditActionContext) {
        final Map jsonObject = new LinkedHashMap<>();
        jsonObject.put("who", auditActionContext.getPrincipal());
        jsonObject.put("what", auditActionContext.getResourceOperatedUpon());
        jsonObject.put("action", auditActionContext.getActionPerformed());
        jsonObject.put("application", auditActionContext.getApplicationCode());
        jsonObject.put("when", auditActionContext.getWhenActionWasPerformed().toString());
        jsonObject.put("clientIpAddress", auditActionContext.getClientIpAddress());
        jsonObject.put("serverIpAddress", auditActionContext.getServerIpAddress());
        return jsonObject;
    }
}
