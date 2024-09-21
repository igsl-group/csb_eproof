package com.hkgov.csb.eproof.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hkgov.csb.eproof.entity.enums.CertStage;
import com.hkgov.csb.eproof.entity.enums.CertStatus;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.hkgov.csb.eproof.constants.Constants.DATE_PATTERN;
import static com.hkgov.csb.eproof.constants.Constants.DATE_TIME_PATTERN;

/**
 * DTO for {@link com.hkgov.csb.eproof.entity.CertInfo}
 */
@Setter
@Getter
public class CertInfoDto implements Serializable {
    Long id;
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    LocalDateTime createdDate;
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    LocalDateTime modifiedDate;
    String modifiedBy;
    String createdBy;

    @JsonFormat(pattern = DATE_PATTERN)
    LocalDate revokeDate;
    String examProfileSerialNo;
    @JsonFormat(pattern = DATE_PATTERN)
    LocalDate examDate;
    String name;
//    String cname;
    String hkid;
    String passportNo;
    String email;
    String blnstGrade;
    String ueGrade;
    String ucGrade;
    String atGrade;
    Boolean passed;
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    LocalDateTime actualSignTime;
    @JsonFormat(pattern = DATE_PATTERN)
    LocalDate actualEmailSendTime;
    String remark;
    CertStage certStage;
    CertStatus certStatus;
    Boolean onHold;
    Boolean valid;
    String onHoldRemark;
    String letterType;
    ExamProfileDto examProfile;
    CertEproofDto certEproof;
    List<FileDto> pdfList;
    String url;
    GcisBatchEmailDto gcisBatchEmail;
}