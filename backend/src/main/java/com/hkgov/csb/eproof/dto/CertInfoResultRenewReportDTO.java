package com.hkgov.csb.eproof.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CertInfoResultRenewReportDTO {
  
  private String candidateName;
  private String hkidNumber;
  private String passportNumber;
  private String result;
  private String oldValue;
  private String newValue;
  private String remarks;
  private LocalDate modifiedDate;
  
}