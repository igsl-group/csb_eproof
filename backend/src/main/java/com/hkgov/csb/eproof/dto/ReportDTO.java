package com.hkgov.csb.eproof.dto;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportDTO {
    private String reportType;
    private LocalDate start;
    private LocalDate end;
    private String year;
    private String examSerialNumber;
}
