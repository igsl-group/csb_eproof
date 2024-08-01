package com.hkgov.csb.eproof.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import static com.hkgov.csb.eproof.constants.Constants.DATE_TIME_PATTERN;

@Getter
@Setter
public class AuditLogListDto {
    private Long id;
    private String ipAddress;
    private String computerInformation;
    private String logDetails;
    private String logAction;
    private String createdBy;
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime createdDate;
}
