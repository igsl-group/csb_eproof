package com.hkgov.csb.eproof.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.hkgov.csb.eproof.constants.Constants.DATE_PATTERN;

@Getter
@Setter
public class CertSearchDto {
    private String examProfileSerialNo;
    private String canName;
//    private String canCName;
    private String canEmail;
    private String hkid;
    private String passportNo;

    private String blnstGrade;
    private String ueGrade;
    private String ucGrade;
    private String atGrade;

    private Boolean certValid;
    private Boolean onHold;
    private String letterType;

    @JsonFormat(pattern = DATE_PATTERN)
    private LocalDate examDateFrom;

    @JsonFormat(pattern = DATE_PATTERN)
    private LocalDate examDateTo;
}
