package com.hkgov.csb.eproof.entity.enums;

import com.hkgov.csb.eproof.constants.enums.ExceptionEnums;
import com.hkgov.csb.eproof.exception.GenericException;
import org.thymeleaf.util.StringUtils;

public enum GradeEnums {
    At_GRADE("at_grade"),
    BL_GRADE("blnst_grade"),
    UC_GRADE("uc_grade"),
    UE_GRADE("ue_grade"),
    ;


    private final String grade;


    public String getGrade() {
        return grade;
    }
    GradeEnums(String grade) {
        this.grade = grade;
    }

    public static GradeEnums getGradeType(String grade) {
        for (GradeEnums value : values()) {
            if (StringUtils.equals(grade, value.getGrade())) {
                return value;
            }
        }
        throw new GenericException(ExceptionEnums.SUBJECT_NOT_EXIST);
    }
}
