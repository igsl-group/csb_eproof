package com.hkgov.csb.eproof.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hkgov.csb.eproof.constants.Constants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "exam_profile")
@Getter
@Setter
public class ExamProfile extends BaseEntity{

    @Id
    @Column(name = "serial_no")
    private String serialNo;

    @Column(name = "exam_date")
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate examDate;

    @Column(name = "result_letter_date")
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate resultLetterDate;

    @Column(name = "announce_date")
    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime announceDate;

    @Column(name = "location")
    private String location;

    @Column(name = "status")
    private String status;

    @Column(name = "is_freezed")
    private Boolean isFreezed;

}