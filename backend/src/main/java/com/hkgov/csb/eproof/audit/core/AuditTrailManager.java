package com.hkgov.csb.eproof.audit.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.hkgov.csb.eproof.audit.common.Cleanable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

/**
 * An interface used to make an audit trail record.
 */
public interface AuditTrailManager extends Cleanable {
    Logger LOG = LoggerFactory.getLogger(AuditTrailManager.class);

    enum WhereClauseFields {
        DATE,
        PRINCIPAL
    }

    ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules()
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true)
        .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

    static String toJson(final Object arg) {
        try {
            return MAPPER.writeValueAsString(arg);
        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * Make an audit trail record. Implementations could use any type of back end medium to serialize audit trail
     * data i.e. RDBMS, log file, IO stream, SMTP, JMS queue or what ever else imaginable.
     * <p>
     * This concept is somewhat similar to log4j Appender.
     *
     * @param auditActionContext the audit action context
     */
    void record(AuditActionContext auditActionContext);

    /**
     * Gets audit records since.
     *
     * @param whereClause the where clause
     * @return the audit records since
     */
    Set<? extends AuditActionContext> getAuditRecords(Map<WhereClauseFields, Object> whereClause);

    /**
     * Remove all.
     */
    default void removeAll() {}

    @Override
    default void clean() {
    }

    default void setAuditFormat(AuditFormats auditFormat) {
    }

    enum AuditFormats {
        DEFAULT {
            @Override
            public String serialize(final Object object) {
                return object.toString();
            }
        },
        JSON {
            @Override
            public String serialize(final Object object) {
                return AuditTrailManager.toJson(object);
            }
        };
        public abstract String serialize(Object object);
    }
}
