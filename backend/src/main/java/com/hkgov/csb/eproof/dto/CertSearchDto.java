package com.hkgov.csb.eproof.dto;


import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CertSearchDto {
    private String examProfileSerialNo;
    private String canName;
    private String canCName;
    private String canEmail;
    private String hkid;
    private String passportNo;

    private String blnstGrade;
    private String ueGrade;
    private String ucGrade;
    private String atGrade;

}
