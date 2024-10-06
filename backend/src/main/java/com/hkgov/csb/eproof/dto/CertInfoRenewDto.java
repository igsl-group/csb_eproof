package com.hkgov.csb.eproof.dto;

import com.hkgov.csb.eproof.entity.File;
import com.hkgov.csb.eproof.entity.enums.CertStage;
import com.hkgov.csb.eproof.entity.enums.CertStatus;
import com.hkgov.csb.eproof.entity.enums.CertType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CertInfoRenewDto {
    private Long id;


    private Long certInfoId;

    private CertType type;


    private String oldName;


//    private String oldCname;


    private String oldHkid;

    private String oldPassport;

    private String oldEmail;

    private String oldBlGrade;

    private String oldUeGrade;

    private String oldUcGrade;

    private String oldAtGrade;

    private String newName;

//    private String newCname;

    private String newHkid;

    private String newPassport;

    private String newEmail;

    private String newBlGrade;

    private String newUeGrade;

    private String newUcGrade;

    private String newAtGrade;

    private String remark;

    private CertStage certStage;

    private CertStatus certStatus;

    private CertEproofRenewDto certEproofRenew;

    private Boolean done;

    private String oldLetterType;
    private String newLetterType;

    private Boolean isDelete;

    private List<File> pdfList;

    private CertInfoDto certInfo;

}
