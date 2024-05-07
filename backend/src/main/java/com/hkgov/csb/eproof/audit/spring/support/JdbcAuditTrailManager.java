package com.hkgov.csb.eproof.audit.spring.support;

import com.hkgov.csb.eproof.audit.core.AuditActionContext;
import com.hkgov.csb.eproof.audit.core.AuditTrailManager;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionOperations;
import org.springframework.util.Assert;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p>Implementation of {@link AuditTrailManager} to persist the
 * audit trail to the  AUDIT_TRAIL table in the Oracle data base.
 * </p>
 * <pre>
 * CREATE TABLE COM_AUDIT_TRAIL
 * (
 *  AUD_USER      VARCHAR2(100) NOT NULL,
 *  AUD_CLIENT_IP VARCHAR(15)   NOT NULL,
 *  AUD_SERVER_IP VARCHAR(15)   NOT NULL,
 *  AUD_RESOURCE  VARCHAR2(100) NOT NULL,
 *  AUD_ACTION    VARCHAR2(100) NOT NULL,
 *  APPLIC_CD     VARCHAR2(5)   NOT NULL,
 *  AUD_DATE      TIMESTAMP     NOT NULL
 * )
 * </pre>
 */
public class JdbcAuditTrailManager extends NamedParameterJdbcDaoSupport implements AuditTrailManager, DisposableBean {

    private static final String INSERT_SQL_TEMPLATE = "INSERT INTO %s " +
                                                      "(AUD_USER, AUD_CLIENT_IP, AUD_SERVER_IP, AUD_RESOURCE, AUD_ACTION, APPLIC_CD, AUD_DATE, AUD_USERAGENT) " +
                                                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String DELETE_SQL_TEMPLATE = "DELETE FROM %s %s";

    private static final int DEFAULT_COLUMN_LENGTH = 100;

    /**
     * Logger instance
     */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Instance of TransactionTemplate to manually execute a transaction since
     * threads are not in the same transaction.
     */
    @NotNull
    private final TransactionOperations transactionTemplate;

    @NotNull
    @Size(min = 1)
    private String tableName = "COM_AUDIT_TRAIL";

    @Min(50)
    private int columnLength = DEFAULT_COLUMN_LENGTH;

    private String selectByDateSqlTemplate = "SELECT * FROM %s WHERE %s ORDER BY AUD_DATE DESC";

    private String dateFormatterPattern = "yyyy-MM-dd 00:00:00.000000";

    /**
     * ExecutorService that has one thread to asynchronously save requests.
     *
     * You can configure one with an {@link org.springframework.scheduling.concurrent.ThreadPoolExecutorFactoryBean}.
     */
    @NotNull
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private boolean defaultExecutorService = true;

    private boolean asynchronous = true;

    /**
     * Criteria used to determine records that should be deleted on cleanup
     */
    private WhereClauseMatchCriteria cleanupCriteria = new NoMatchWhereClauseMatchCriteria();

    public JdbcAuditTrailManager(final TransactionOperations transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    public void setAsynchronous(final boolean asynchronous) {
        this.asynchronous = asynchronous;
    }

    @Override
    public void record(final AuditActionContext auditActionContext) {
        final var command = new LoggingTask(auditActionContext,
            this.transactionTemplate, this.columnLength);
        if (this.asynchronous) {
            this.executorService.execute(command);
        } else {
            command.run();
        }
    }

    public void setTableName(final String tableName) {
        this.tableName = tableName;
    }

    public void setCleanupCriteria(final WhereClauseMatchCriteria criteria) {
        this.cleanupCriteria = criteria;
    }

    public void setExecutorService(final ExecutorService executorService) {
        this.executorService = executorService;
        this.defaultExecutorService = false;
    }

    public void setColumnLength(final int columnLength) {
        this.columnLength = columnLength;
    }

    /**
     * We only shut down the default executor service.  We assume, that if you've injected one, its being managed elsewhere.
     */
    @Override
    public void destroy() {
        if (this.defaultExecutorService) {
            this.executorService.shutdown();
        }
    }

    @Override
    public void clean() {
        this.transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(final TransactionStatus transactionStatus) {
                final var sql = String.format(DELETE_SQL_TEMPLATE, tableName, cleanupCriteria);
                final var params = cleanupCriteria.getParameterValues();
                JdbcAuditTrailManager.this.logger.info("Cleaning audit records with query " + sql);
                JdbcAuditTrailManager.this.logger.debug("Query parameters: " + params);
                final var count = getJdbcTemplate().update(sql, params.toArray());
                JdbcAuditTrailManager.this.logger.info(count + " records deleted.");
            }
        });
    }

    @Override
    public void removeAll() {
        this.transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(final TransactionStatus transactionStatus) {
                final var sql = String.format(DELETE_SQL_TEMPLATE, tableName, "");
                final var count = getJdbcTemplate().update(sql);
                JdbcAuditTrailManager.this.logger.info(count + " records deleted.");
            }
        });
    }

    @Override
    public Set<? extends AuditActionContext> getAuditRecords(final Map<WhereClauseFields, Object> whereClause) {

        var builder = new StringBuilder("1=1 ");
        if (whereClause.containsKey(WhereClauseFields.DATE)) {
            final var formatter = DateTimeFormatter.ofPattern(this.dateFormatterPattern);
            var sinceDate = (LocalDate) whereClause.get(WhereClauseFields.DATE);
            builder.append(String.format("AND AUD_DATE>='%s' ", sinceDate.format(formatter)));
        }
        if (whereClause.containsKey(WhereClauseFields.PRINCIPAL)) {
            var principal = whereClause.get(WhereClauseFields.PRINCIPAL).toString();
            builder.append(String.format("AND AUD_USER='%s' ", principal));
        }
        return getAuditRecordsSince(builder);
    }

    public void setDateFormatterPattern(final String dateFormatterPattern) {
        this.dateFormatterPattern = dateFormatterPattern;
    }

    public void setSelectByDateSqlTemplate(final String selectByDateSqlTemplate) {
        this.selectByDateSqlTemplate = selectByDateSqlTemplate;
    }

    protected class LoggingTask implements Runnable {

        private final AuditActionContext auditActionContext;

        private final TransactionOperations transactionTemplate;

        private final int columnLength;

        public LoggingTask(final AuditActionContext auditActionContext,
                           final TransactionOperations transactionTemplate, final int columnLength) {
            this.auditActionContext = auditActionContext;
            this.transactionTemplate = transactionTemplate;
            this.columnLength = columnLength;
        }

        @Override
        public void run() {
            this.transactionTemplate
                .execute(new TransactionCallbackWithoutResult() {
                    @Override
                    protected void doInTransactionWithoutResult(final TransactionStatus transactionStatus) {
                        final var userId =
                            auditActionContext.getPrincipal().length() <= columnLength ? auditActionContext.getPrincipal() : auditActionContext.getPrincipal().substring(0, columnLength);
                        final var resource = auditActionContext.getResourceOperatedUpon().length() <= columnLength ? auditActionContext.getResourceOperatedUpon() :
                            auditActionContext.getResourceOperatedUpon().substring(0, columnLength);
                        final var action = auditActionContext.getActionPerformed().length() <= columnLength ? auditActionContext.getActionPerformed() :
                            auditActionContext.getActionPerformed().substring(0, columnLength);

                        getJdbcTemplate()
                            .update(
                                String.format(INSERT_SQL_TEMPLATE, tableName),
                                userId,
                                auditActionContext.getClientIpAddress(),
                                auditActionContext.getServerIpAddress(),
                                resource,
                                action,
                                auditActionContext.getApplicationCode(),
                                auditActionContext.getWhenActionWasPerformed(),
                                auditActionContext.getUserAgent());
                    }
                });
        }
    }

    private Set<? extends AuditActionContext> getAuditRecordsSince(final StringBuilder where) {
        return this.transactionTemplate.execute((TransactionCallback<Set>) transactionStatus -> {
            final var sql = String.format(this.selectByDateSqlTemplate, tableName, where);
            Set<AuditActionContext> results = new LinkedHashSet<>();
            getJdbcTemplate().query(sql, resultSet -> {
                results.add(getAuditActionContext(resultSet));
            });
            return results;
        });
    }

    private AuditActionContext getAuditActionContext(final ResultSet resultSet) throws SQLException {
        final var principal = resultSet.getString("AUD_USER");
        final var resource = resultSet.getString("AUD_RESOURCE");
        final var clientIp = resultSet.getString("AUD_CLIENT_IP");
        final var serverIp = resultSet.getString("AUD_SERVER_IP");
        final var audDate = resultSet.getDate("AUD_DATE");
        final var appCode = resultSet.getString("APPLIC_CD");
        final var action = resultSet.getString("AUD_ACTION");
        final var userAgent = resultSet.getString("AUD_USERAGENT");

        Assert.notNull(principal, "AUD_USER cannot be null");
        Assert.notNull(resource, "AUD_RESOURCE cannot be null");
        Assert.notNull(clientIp, "AUD_CLIENT_IP cannot be null");
        Assert.notNull(serverIp, "AUD_SERVER_IP cannot be null");
        Assert.notNull(audDate, "AUD_DATE cannot be null");
        Assert.notNull(appCode, "APPLIC_CD cannot be null");
        Assert.notNull(action, "AUD_ACTION cannot be null");

        final var audit = new AuditActionContext(principal, resource,
            action, appCode, LocalDateTime.ofInstant(audDate.toInstant(), ZoneId.systemDefault()), clientIp, serverIp, userAgent);
        return audit;
    }
}
