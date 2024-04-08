package com.hkgov.ceo.pms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_log")
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "audit_log_id")
    private Long auditLogId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "principal")
    private String principal;

    @Column(name = "action")
    private String action;

    @Column(name = "resource")
    private String resource;

    @Column(name = "action_date_time")
    private LocalDateTime actionDateTime;

    @Column(name = "node")
    private String node;

    @Column(name = "client_ip_address")
    private String clientIpAddress;

    @Column(name = "server_ip_address")
    private String serverIpAddress;

    @Column(name = "userAgent")
    private String userAgent;

    @Column(name = "url")
    private String url;

    @Column(name = "method")
    private String method;

    @Lob
    @Column(name = "request_params", columnDefinition = "LONGTEXT")
    private String requestParams;

    @Lob
    @Column(name = "request_body", columnDefinition = "LONGTEXT")
    private String requestBody;

    @Lob
    @Column(name = "response_body", columnDefinition = "LONGTEXT")
    private String responseBody;

    @Column(name = "status")
    private String status;

    @Column(name = "error_message")
    private String errorMessage;

    public Long getAuditLogId() {
        return auditLogId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public LocalDateTime getActionDateTime() {
        return actionDateTime;
    }

    public void setActionDateTime(LocalDateTime actionDateTime) {
        this.actionDateTime = actionDateTime;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public String getClientIpAddress() {
        return clientIpAddress;
    }

    public void setClientIpAddress(String clientIpAddress) {
        this.clientIpAddress = clientIpAddress;
    }

    public String getServerIpAddress() {
        return serverIpAddress;
    }

    public void setServerIpAddress(String serverIpAddress) {
        this.serverIpAddress = serverIpAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(String requestParams) {
        this.requestParams = requestParams;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public static class Builder {
        private Long userId;
        private String principal;
        private String action;
        private String resource;
        private LocalDateTime actionDateTime;
        private String node;
        private String clientIpAddress;
        private String serverIpAddress;
        private String userAgent;
        private String url;
        private String method;
        private String requestParams;
        private String requestBody;
        private String responseBody;
        private String status;
        private String errorMessage;

        private Builder() {
        }

        public static Builder create() {
            return new Builder();
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder principal(String principal) {
            this.principal = principal;
            return this;
        }

        public Builder action(String action) {
            this.action = action;
            return this;
        }

        public Builder resource(String resource) {
            this.resource = resource;
            return this;
        }

        public Builder actionDateTime(LocalDateTime actionDateTime) {
            this.actionDateTime = actionDateTime;
            return this;
        }

        public Builder node(String node) {
            this.node = node;
            return this;
        }

        public Builder clientIpAddress(String clientIpAddress) {
            this.clientIpAddress = clientIpAddress;
            return this;
        }

        public Builder serverIpAddress(String serverIpAddress) {
            this.serverIpAddress = serverIpAddress;
            return this;
        }

        public Builder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder method(String method) {
            this.method = method;
            return this;
        }

        public Builder requestParams(String requestParams) {
            this.requestParams = requestParams;
            return this;
        }

        public Builder requestBody(String requestBody) {
            this.requestBody = requestBody;
            return this;
        }

        public Builder responseBody(String responseBody) {
            this.responseBody = responseBody;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public AuditLog build() {
            AuditLog auditLog = new AuditLog();
            auditLog.setUserId(userId);
            auditLog.setPrincipal(principal);
            auditLog.setAction(action);
            auditLog.setResource(resource);
            auditLog.setActionDateTime(actionDateTime);
            auditLog.setNode(node);
            auditLog.setClientIpAddress(clientIpAddress);
            auditLog.setServerIpAddress(serverIpAddress);
            auditLog.setUserAgent(userAgent);
            auditLog.setUrl(url);
            auditLog.setMethod(method);
            auditLog.setRequestParams(requestParams);
            auditLog.setRequestBody(requestBody);
            auditLog.setResponseBody(responseBody);
            auditLog.setStatus(status);
            auditLog.setErrorMessage(errorMessage);
            return auditLog;
        }
    }
}