package com.hkgov.csb.eproof.audit.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Immutable container holding the core elements of an audit-able action that need to be recorded
 * as an audit trail record.
 */
public class AuditActionContext implements Serializable {

    /**
     * Unique Id for serialization.
     */
    @Serial
    private static final long serialVersionUID = -3530737409883959089L;

    @JsonProperty
    private final Long userId;

    /**
     * This is <i>WHO</i>
     */
    @JsonProperty
    private final String principal;

    /**
     * This is <i>WHAT</i>
     */
    @JsonProperty
    private final String resourceOperatedUpon;

    /**
     * This is <i>ACTION</i>
     */
    @JsonProperty
    private final String actionPerformed;

    /**
     * This is <i>STATUS/i>
     */
    @JsonProperty
    private final String status;

    /**
     * This is <i>Application from which operation has been performed</i>
     */
    @JsonProperty
    private final String applicationCode;

    /**
     * This is <i>WHEN</i>
     */
    @JsonProperty
    private final LocalDateTime whenActionWasPerformed;

    /**
     * Client IP Address
     */
    @JsonProperty
    private final String clientIpAddress;

    /**
     * Server IP Address
     */
    @JsonProperty
    private final String serverIpAddress;

    @JsonProperty
    private final String userAgent;

    @JsonProperty
    private final String requestUrl;

    @JsonProperty
    private final String requestMethod;

    @JsonProperty
    private final String requestHeaders;

    @JsonProperty
    private final String requestPayload;

    @JsonProperty
    private final String returnValue;

    @JsonProperty
    private final String errormessage;

    @JsonCreator
    public AuditActionContext(@JsonProperty("principal") final String principal,
                              @JsonProperty("resourceOperatedUpon") final String resourceOperatedUpon,
                              @JsonProperty("actionPerformed") final String actionPerformed,
                              @JsonProperty("applicationCode") final String applicationCode,
                              @JsonProperty("whenActionWasPerformed") final LocalDateTime whenActionWasPerformed,
                              @JsonProperty("clientIpAddress") final String clientIpAddress,
                              @JsonProperty("serverIpAddress") final String serverIpAddress,
                              @JsonProperty("userAgent") final String userAgent) {
        this.userId = null;
        this.principal = principal;
        this.resourceOperatedUpon = resourceOperatedUpon;
        this.actionPerformed = actionPerformed;
        this.status = null;
        this.applicationCode = applicationCode;
        this.whenActionWasPerformed = whenActionWasPerformed;
        this.clientIpAddress = clientIpAddress;
        this.serverIpAddress = serverIpAddress;
        this.userAgent = userAgent;
        this.requestUrl = null;
        this.requestMethod = null;
        this.requestHeaders = null;
        this.requestPayload = null;
        this.returnValue = null;
        this.errormessage = null;
    }

    @JsonCreator
    public AuditActionContext(@JsonProperty("userId") final Long userId,
                              @JsonProperty("principal") final String principal,
                              @JsonProperty("resourceOperatedUpon") final String resourceOperatedUpon,
                              @JsonProperty("actionPerformed") final String actionPerformed,
                              @JsonProperty("status") final String status,
                              @JsonProperty("applicationCode") final String applicationCode,
                              @JsonProperty("whenActionWasPerformed") final LocalDateTime whenActionWasPerformed,
                              @JsonProperty("clientIpAddress") final String clientIpAddress,
                              @JsonProperty("serverIpAddress") final String serverIpAddress,
                              @JsonProperty("userAgent") final String userAgent,
                              @JsonProperty("requestUrl") final String requestUrl,
                              @JsonProperty("requestMethod") final String requestMethod,
                              @JsonProperty("requestHeaders") final String requestHeaders,
                              @JsonProperty("requestPayload") final String requestPayload,
                              @JsonProperty("returnValue") final String returnValue,
                              @JsonProperty("errormessage") final String errormessage
    ) {
        this.userId = userId;
        this.principal = principal;
        this.resourceOperatedUpon = resourceOperatedUpon;
        this.actionPerformed = actionPerformed;
        this.status = status;
        this.applicationCode = applicationCode;
        this.whenActionWasPerformed = whenActionWasPerformed;
        this.clientIpAddress = clientIpAddress;
        this.serverIpAddress = serverIpAddress;
        this.userAgent = userAgent;
        this.requestUrl = requestUrl;
        this.requestMethod = requestMethod;
        this.requestHeaders = requestHeaders;
        this.requestPayload = requestPayload;
        this.returnValue = returnValue;
        this.errormessage = errormessage;
    }

    public Long getUserId() {
        return userId;
    }

    public String getPrincipal() {
        return principal;
    }

    public String getResourceOperatedUpon() {
        return resourceOperatedUpon;
    }

    public String getActionPerformed() {
        return actionPerformed;
    }

    public String getStatus() {
        return status;
    }

    public String getApplicationCode() {
        return applicationCode;
    }

    public LocalDateTime getWhenActionWasPerformed() {
        return whenActionWasPerformed;
    }

    public String getClientIpAddress() {
        return this.clientIpAddress;
    }

    public String getServerIpAddress() {
        return this.serverIpAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public String getRequestHeaders() {
        return requestHeaders;
    }

    public String getRequestPayload() {
        return requestPayload;
    }

    public String getReturnValue() {
        return returnValue;
    }

    public String getErrormessage() {
        return errormessage;
    }
}
