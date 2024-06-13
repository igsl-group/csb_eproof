package com.hkgov.csb.eproof.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class ExamProfileDto {

    private String serialNo;
    private LocalDate examDate;
    private LocalDateTime announceDate;

    private String location;

    private String status;

    private Boolean isFreezed;
}
