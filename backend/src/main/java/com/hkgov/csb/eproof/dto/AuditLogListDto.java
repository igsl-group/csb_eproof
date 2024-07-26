package com.hkgov.csb.eproof.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuditLogListDto {
    private Long id;
    private String ipAddress;
    private String computerInformation;
    private String logDetails;
    private String logAction;
    private String createdBy;
}
