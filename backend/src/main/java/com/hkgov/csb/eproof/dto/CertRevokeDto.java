package com.hkgov.csb.eproof.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

import static com.hkgov.csb.eproof.constants.Constants.DATE_TIME_PATTERN;

@Getter
@Setter
public class CertRevokeDto {
    private Long id;
    private String remark;
    private String emailTarget;
    private String emailContent;

    private String hkid;
    private String passportNo;
    private String name;
    private String createdBy;
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime createdDate;
    private String type;
    private String status;
    private String approver;
    List<CertInfoDto> certInfos;
}
