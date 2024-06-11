package com.hkgov.csb.eproof.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hkgov.csb.eproof.constants.Constants;
import com.hkgov.csb.eproof.entity.enums.CertStage;
import com.hkgov.csb.eproof.entity.enums.CertStatus;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "cert_info")
@Getter
@Setter
public class CertInfo extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "exam_profile_serial")
    private String examProfileSerialNo;

    @Column(name = "exam_date")
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate examDate;

    @Column(name = "name")
    private String name;

    @Column(name = "cname")
    private String cname;

    @Column(name = "hkid")
    private String hkid;

    @Column(name = "passportNo")
    private String passportNo;

    @Column(name = "email")
    private String email;

    @Column(name = "blnst_grade")
    private String blnstGrade;

    @Column(name = "ue_grade")
    private String ueGrade;

    @Column(name = "uc_grade")
    private String ucGrade;

    @Column(name = "at_grade")
    private String atGrade;


    @Column(name = "is_passed")
    private Boolean passed;

    @Column(name = "actual_sign_time")
    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime actualSignTime;

    @Column(name = "actual_email_send_time")
    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime actualEmailSendTime;

    @Column(name = "remark")
    private String remark;

    @Column(name = "cert_stage")
    @Enumerated(EnumType.STRING)
    private CertStage certStage;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private CertStatus certStatus;

    @Column(name = "on_hold")
    private Boolean onHold;

    @Column(name = "is_valid")
    private Boolean valid;

    @Column(name = "on_hold_remark" ,columnDefinition="LONGTEXT")
    private String onHoldRemark;

    @Column(name = "letter_type")
    private String letterType;

    // This getter functions needed to be used by Document merging. MUST NOT DELETE
    public String getHkidOrPassport(){
        if (StringUtils.isNotEmpty(hkid)){
            return hkid;
        } else{
            return passportNo;
        }
    }

    // Mapped tables
    @ManyToOne
    @JoinColumn(name = "exam_profile_serial", insertable = false, updatable = false)
    private ExamProfile examProfile;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "cert_pdf",
            joinColumns = @JoinColumn(name = "cert_info_id"),
            inverseJoinColumns = @JoinColumn(name = "file_id")
    )
    private List<File> pdfList;
}