package com.hkgov.csb.eproof.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExamResultReportDTO {
  private String examProfileSerial;
  private long ucTotalCandidate;
  private double ucNoOfL2;
  private double ucNoOfL1;
  private long ueTotalCandidate;
  private double ueNoOfL2;
  private double ueNoOfL1;
  private long atTotalCandidate;
  private double atPassRate;
  private double atFailRate;
  private long blnstTotalCandidate;
  private double blnstPassRate;
  private double blnstFailRate;
}
