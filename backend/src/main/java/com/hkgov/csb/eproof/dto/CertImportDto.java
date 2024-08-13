package com.hkgov.csb.eproof.dto;

import com.opencsv.bean.CsvBindByName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CertImportDto {
    @CsvBindByName(column = "doe")
    private String examDate = "";
    @CsvBindByName(column = "name")
    private String name= "";
    @CsvBindByName(column = "hkid")
    private String hkid= "";
    @CsvBindByName(column = "passport")
    private String passportNo= "";
    @CsvBindByName(column = "email")
    private String email= "";
    @CsvBindByName(column = "bl_grade")
    private String blnstGrade= "";
    @CsvBindByName(column = "ue_grade")
    private String ueGrade= "";
    @CsvBindByName(column = "uc_grade")
    private String ucGrade= "";
    @CsvBindByName(column = "at_grade")
    private String atGrade= "";
    @CsvBindByName(column = "lett_type")
    private String letterType= "";
}
