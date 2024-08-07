package com.hkgov.csb.eproof.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateResultDto {
    private String newUeGrade;
    private String newUcGrade;
    private String newAtGrade;
    private String newBlnstGrade;
    private String newLetterType;
    private String remark;
}
