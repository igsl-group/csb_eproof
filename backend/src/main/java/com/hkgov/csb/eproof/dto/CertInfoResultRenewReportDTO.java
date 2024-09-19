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
  private String resultUpdated;
  private LocalDate examDate;
  private String oldAtGrade;
  private String oldBlGrade;
  private String oldUcGrade;
  private String oldUeGrade;
  private String newAtGrade;
  private String newBlGrade;
  private String newUcGrade;
  private String newUeGrade;
  private String remarks;
  private LocalDate modifiedDate;
  
}