package com.hkgov.csb.eproof.entity;

import com.hkgov.csb.eproof.entity.enums.CertStage;
import com.hkgov.csb.eproof.entity.enums.CertStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "cret_info")
@Getter
@Setter

public class CertInfo extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @MapsId("serialNo")
    @JoinColumn(name = "exam_profile_serial")
    private ExamProfile examProfile;

    @Column(name = "can_serial")
    private String candidateSerialNo;

    @Column(name = "exam_date")
    private LocalDate examDate;

    @Column(name = "name")
    private String name;

    @Column(name = "cname")
    private String cname;

    @Column(name = "hkid")
    private String hkid;

    @Column(name = "passport")
    private String passport;

    @Column(name = "email")
    private String email;

    @Column(name = "bl_grade")
    private String blGrade;

    @Column(name = "ue_grade")
    private String ueGrade;

    @Column(name = "uc_grade")
    private String ucGrade;

    @Column(name = "at_grade")
    private String atGrade;

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

    @Column(name = "on_hold_remark" ,columnDefinition="LONGTEXT")
    private String onHoldRemark;


}