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
public class AuditLogSearchDto implements Serializable {
    private final String principal;
    private final String action;
    private final String resource;

    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private final LocalDateTime actionDateTime;
    private final String node;
    private final String clientIpAddress;
    private final String requestParams;
    private final String status;

    public AuditLogSearchDto(String principal, String action, String resource, LocalDateTime actionDateTime, String node, String clientIpAddress, String requestParams, String status) {
        this.principal = principal;
        this.action = action;
        this.resource = resource;
        this.actionDateTime = actionDateTime;
        this.node = node;
        this.clientIpAddress = clientIpAddress;
        this.requestParams = requestParams;
        this.status = status;
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

    public String getRequestParams() {
        return requestParams;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuditLogSearchDto entity = (AuditLogSearchDto) o;
        return Objects.equals(this.principal, entity.principal) &&
                Objects.equals(this.action, entity.action) &&
                Objects.equals(this.resource, entity.resource) &&
                Objects.equals(this.actionDateTime, entity.actionDateTime) &&
                Objects.equals(this.node, entity.node) &&
                Objects.equals(this.clientIpAddress, entity.clientIpAddress) &&
                Objects.equals(this.requestParams, entity.requestParams) &&
                Objects.equals(this.status, entity.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(principal, action, resource, actionDateTime, node, clientIpAddress, requestParams, status);
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
                "headers = " + requestParams + ", " +
                "status = " + status + ")";
    }
}