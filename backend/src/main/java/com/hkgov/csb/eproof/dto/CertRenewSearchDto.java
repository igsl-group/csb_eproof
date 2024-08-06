package com.hkgov.csb.eproof.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CertRenewSearchDto {
    private String oldName;
    private String oldCname;
    private String oldHkid;
    private String oldPassport;
    private String oldEmail;
    private String oldBlGrade;
    private String oldUeGrade;
    private String oldUcGrade;
    private String oldAtGrade;
    private String newName;
    private String newCname;
    private String newHkid;
    private String newPassport;
    private String newEmail;
    private String newBlGrade;
    private String newUeGrade;
    private String newUcGrade;
    private String newAtGrade;
}
