package com.hkgov.csb.eproof.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

import static com.hkgov.csb.eproof.constants.Constants.DATE_PATTERN;

@Entity
@Table(name = "exam_profile")
@Getter
@Setter
public class ExamProfile extends BaseEntity{

    @Id
    @Column(name = "serial_no")
    private String serialNo;

    @JsonFormat(pattern = DATE_PATTERN)
    @Column(name = "exam_date")
    private LocalDate examDate;

    @JsonFormat(pattern = DATE_PATTERN)
    @Column(name = "result_letter_date")
    private LocalDate resultLetterDate;

    @JsonFormat(pattern = DATE_PATTERN)
    @Column(name = "effective_date")
    private LocalDate effectiveDate;

    @Column(name = "planned_email_issuance_date")
    @JsonFormat(pattern = DATE_PATTERN)
    private LocalDate plannedEmailIssuanceDate;

    @Column(name = "location")
    private String location;

    @Column(name = "status")
    private String status;

    @Column(name = "is_freezed")
    private Boolean isFreezed;

}