package com.hkgov.csb.eproof.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ExamProfileCreateDto {
    private String serialNo;
    private LocalDateTime examDate;
    private LocalDateTime plannedEmailIssuanceDate;
    private String location;
    private LocalDateTime resultLetterDate;
}
