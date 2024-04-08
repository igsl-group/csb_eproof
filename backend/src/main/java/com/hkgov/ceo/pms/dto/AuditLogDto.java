package com.hkgov.ceo.pms.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hkgov.ceo.pms.entity.AuditLog;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import static com.hkgov.ceo.pms.config.Constants.DATE_TIME_PATTERN;

/**
 * A DTO for the {@link AuditLog} entity
 */
public class AuditLogDto implements Serializable {
    private final String principal;
    private final String action;
    private final String resource;

    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private final LocalDateTime actionDateTime;
    private final String node;
    private final String clientIpAddress;
    private final String serverIpAddress;
    private final String userAgent;
    private final String url;
    private final String method;
    private final String requestParams;
    private final String requestBody;
    private final String responseBody;
    private final String status;
    private final String errorMessage;

    public AuditLogDto(String principal, String action, String resource, LocalDateTime actionDateTime, String node, String clientIpAddress, String serverIpAddress, String userAgent, String url, String method, String requestParams, String requestBody, String responseBody, String status, String errorMessage) {
        this.principal = principal;
        this.action = action;
        this.resource = resource;
        this.actionDateTime = actionDateTime;
        this.node = node;
        this.clientIpAddress = clientIpAddress;
        this.serverIpAddress = serverIpAddress;
        this.userAgent = userAgent;
        this.url = url;
        this.method = method;
        this.requestParams = requestParams;
        this.requestBody = requestBody;
        this.responseBody = responseBody;
        this.status = status;
        this.errorMessage = errorMessage;
    }

    public String getPrincipal() {
        return principal;
    }

    public String getAction() {
        return action;
    }

    public String getResource() {
        return resource;
    }

    public LocalDateTime getActionDateTime() {
        return actionDateTime;
    }

    public String getNode() {
        return node;
    }

    public String getClientIpAddress() {
        return clientIpAddress;
    }

    public String getServerIpAddress() {
        return serverIpAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getUrl() {
        return url;
    }

    public String getMethod() {
        return method;
    }

    public String getRequestParams() {
        return requestParams;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public String getStatus() {
        return status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuditLogDto entity = (AuditLogDto) o;
        return Objects.equals(this.principal, entity.principal) &&
                Objects.equals(this.action, entity.action) &&
                Objects.equals(this.resource, entity.resource) &&
                Objects.equals(this.actionDateTime, entity.actionDateTime) &&
                Objects.equals(this.node, entity.node) &&
                Objects.equals(this.clientIpAddress, entity.clientIpAddress) &&
                Objects.equals(this.serverIpAddress, entity.serverIpAddress) &&
                Objects.equals(this.userAgent, entity.userAgent) &&
                Objects.equals(this.url, entity.url) &&
                Objects.equals(this.method, entity.method) &&
                Objects.equals(this.requestParams, entity.requestParams) &&
                Objects.equals(this.requestBody, entity.requestBody) &&
                Objects.equals(this.responseBody, entity.responseBody) &&
                Objects.equals(this.status, entity.status) &&
                Objects.equals(this.errorMessage, entity.errorMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(principal, action, resource, actionDateTime, node, clientIpAddress, serverIpAddress, userAgent, url, method, requestParams, requestBody, responseBody, status, errorMessage);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "principal = " + principal + ", " +
                "action = " + action + ", " +
                "resource = " + resource + ", " +
                "actionDateTime = " + actionDateTime + ", " +
                "node = " + node + ", " +
                "clientIpAddress = " + clientIpAddress + ", " +
                "serverIpAddress = " + serverIpAddress + ", " +
                "userAgent = " + userAgent + ", " +
                "url = " + url + ", " +
                "method = " + method + ", " +
                "headers = " + requestParams + ", " +
                "requestBody = " + requestBody + ", " +
                "responseBody = " + responseBody + ", " +
                "status = " + status + ", " +
                "errorMessage = " + errorMessage + ")";
    }
}