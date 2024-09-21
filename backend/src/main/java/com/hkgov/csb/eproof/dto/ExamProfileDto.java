package com.hkgov.csb.eproof.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.hkgov.csb.eproof.constants.Constants.DATE_PATTERN;
import static com.hkgov.csb.eproof.constants.Constants.DATE_TIME_PATTERN;

@Getter
@Setter
public class ExamProfileDto {


    private String serialNo;

    @JsonFormat(pattern = DATE_PATTERN)
    private LocalDate examDate;

    @JsonFormat(pattern = DATE_PATTERN)
    private LocalDate resultLetterDate;
    @JsonFormat(pattern = DATE_PATTERN)
    private LocalDate plannedEmailIssuanceDate;
    @JsonFormat(pattern = DATE_PATTERN)
    private LocalDate effectiveDate;


    private String location;


    private String status;
    private Boolean isFreezed;

    @JsonFormat(pattern = DATE_PATTERN)
    private LocalDate actualEmailSendDateFrom;

    @JsonFormat(pattern = DATE_PATTERN)
    private LocalDate actualEmailSendDateTo;

}
