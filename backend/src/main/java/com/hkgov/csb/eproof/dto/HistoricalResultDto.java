package com.hkgov.csb.eproof.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hkgov.csb.eproof.constants.Constants;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.hkgov.csb.eproof.constants.Constants.DATE_TIME_PATTERN;

@Getter
@Setter
public class HistoricalResultDto {
    private Long id;

    private String name;

    private String cname;

    private String hkid;

    private String passport;

    private String email;

    private String blGrade;

    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate blDate;

    private Boolean blVoid;

    private String ueGrade;

    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate ueDate;

    private Boolean ueVoid;

    private String ucGrade;

    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate ucDate;

    private Boolean ucVoid;

    private String atGrade;

    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate atDate;

    private Boolean atVoid;

    private Boolean actionFreeze;

    private String remark;

    private Boolean valid;

    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime createdDate;

    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime modifiedDate;

    private String modifiedBy;

    private String createdBy;
}
