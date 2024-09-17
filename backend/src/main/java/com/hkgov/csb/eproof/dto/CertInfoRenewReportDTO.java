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
public class CertInfoRenewReportDTO {
  
    
      private String candidateName;
      private String hkidNumber;
      private String passportNumber;
      private String personalParticularsUpdated;
      private String oldName;
      private String oldHkid;
      private String oldPassport;
      private String oldEmail;
      private String newName;
      private String newHkid;
      private String newPassport;
      private String newEmail;
      private String remarks;
      private LocalDate modifiedDate;
      
  
  
}