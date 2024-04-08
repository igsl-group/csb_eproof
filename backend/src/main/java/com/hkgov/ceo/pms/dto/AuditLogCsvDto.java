package com.hkgov.ceo.pms.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hkgov.ceo.pms.entity.AuditLog;
import com.opencsv.bean.CsvBindByPosition;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

import static com.hkgov.ceo.pms.config.Constants.DATE_TIME_PATTERN;

/**
 * A DTO for the {@link AuditLog} entity
 */
public class AuditLogCsvDto implements Serializable {
    @CsvBindByPosition(position = 2)
    private final String principal;
    @CsvBindByPosition(position = 5)
    private final String action;
    @CsvBindByPosition(position = 6)
    private final String resource;
    @CsvBindByPosition(position = 0)
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private final LocalDate date;
    @CsvBindByPosition(position = 1)
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private final LocalTime time;
    @CsvBindByPosition(position = 3)
    private final String clientIpAddress;
    @CsvBindByPosition(position = 4)
    private final String status;

    public AuditLogCsvDto(String principal, String action, String resource, LocalDate date, LocalTime time, String clientIpAddress, String status) {
        this.principal = principal;
        this.action = action;
        this.resource = resource;
        this.date = date;
        this.time = time;
        this.clientIpAddress = clientIpAddress;
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

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;
    }

    public String getClientIpAddress() {
        return clientIpAddress;
    }

    public String getStatus() {
        return status;
    }
}