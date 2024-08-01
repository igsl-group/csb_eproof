package com.hkgov.csb.eproof.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

import static com.hkgov.csb.eproof.constants.Constants.DATE_PATTERN;

@Getter
@Setter
public class CertDetailDto {
    String blnstGrade;
    String ueGrade;
    String ucGrade;
    String atGrade;
    @JsonFormat(pattern = DATE_PATTERN)
    LocalDate examDate;
    @JsonFormat(pattern = DATE_PATTERN)
    LocalDate issueDate;
}
