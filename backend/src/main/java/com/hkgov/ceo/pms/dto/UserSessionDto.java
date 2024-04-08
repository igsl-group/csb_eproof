package com.hkgov.ceo.pms.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.time.LocalDateTime;

import static com.hkgov.ceo.pms.config.Constants.DATE_TIME_PATTERN;

/**
 * A DTO for the {@link com.hkgov.ceo.pms.entity.UserSession} entity
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserSessionDto implements Serializable {

    private Long userSessionId;
    private String token;
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime createDate;
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime timeStamp;
    private String clientIpAddress;
    private String userName;
    private String loginId;
    private String connectionTime;
    private Boolean isCurrentUserSession;

    public Boolean getCurrentUserSession() {
        return isCurrentUserSession;
    }

    public void setCurrentUserSession(Boolean currentUserSession) {
        isCurrentUserSession = currentUserSession;
    }

    public Long getUserSessionId() {
        return userSessionId;
    }

    public void setUserSessionId(Long userSessionId) {
        this.userSessionId = userSessionId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public String getClientIpAddress() {
        return clientIpAddress;
    }

    public String getUserName() {
        return userName;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setClientIpAddress(String clientIpAddress) {
        this.clientIpAddress = clientIpAddress;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getConnectionTime() {
        return connectionTime;
    }

    public void setConnectionTime(String connectionTime) {
        this.connectionTime = connectionTime;
    }
}