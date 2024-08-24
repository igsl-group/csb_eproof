package com.hkgov.csb.eproof.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.hkgov.csb.eproof.constants.Constants.DATE_PATTERN;
import static com.hkgov.csb.eproof.constants.Constants.DATE_TIME_PATTERN;

@Builder
@Getter
@Setter
public class ExamProfileSummaryDto {

    private Integer imported;

    private Integer generatePdfTotal;
    private Integer generatePdfPending;
    private Integer generatePdfFailed;
    private Integer generatePdfSuccess;
    private Integer generatePdfInProgress;

    private Integer issuedPdfTotal;
    private Integer issuedPdfPending;
    private Integer issuedPdfInScheduled;
    private Integer issuedPdfFailed;
    private Integer issuedPdfSuccess;
    private Integer issuedPdfInProgress;

    private Integer sendEmailTotal;
    private Integer sendEmailFailed;
    private Integer sendEmailSuccess;
    private Integer sendEmailProgress;
    private Integer sendEmailScheduled;

    private Integer onHoldCaseTotal;
}
