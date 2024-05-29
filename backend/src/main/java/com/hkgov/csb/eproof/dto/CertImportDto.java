package com.hkgov.csb.eproof.dto;

import com.opencsv.bean.CsvBindByName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CertImportDto {
    @CsvBindByName(column = "Exam Date")
    private String examDate;
    @CsvBindByName(column = "Name in English ")
    private String name;
    @CsvBindByName(column = "HKID")
    private String hkid;
    @CsvBindByName(column = "Passport No")
    private String passport;
    @CsvBindByName(column = "Email")
    private String email;
    @CsvBindByName(column = "BL Grade")
    private String blGrade;
    @CsvBindByName(column = "UE Grade")
    private String ueGrade;
    @CsvBindByName(column = "UC Grade")
    private String ucGrade;
    @CsvBindByName(column = "AT Grade")
    private String atGrade;
    @CsvBindByName(column = "Letter Type (Pass/Fail)")
    private String letterType;
}
