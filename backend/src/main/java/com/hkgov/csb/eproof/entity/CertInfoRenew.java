package com.hkgov.csb.eproof.entity;

import com.hkgov.csb.eproof.entity.enums.CertStage;
import com.hkgov.csb.eproof.entity.enums.CertType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "cert_info_renew")
@Getter
@Setter
public class CertInfoRenew extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cert_info_id")
    private Long certInfoId;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private CertType type;

    @Column(name = "old_name")
    private String oldName;

    @Column(name = "old_cname")
    private String oldCname;

    @Column(name = "old_hkid")
    private String oldHkid;

    @Column(name = "old_passport")
    private String oldPassport;

    @Column(name = "old_email")
    private String oldEmail;

    @Column(name = "old_bl_grade")
    private String oldBlGrade;

    @Column(name = "old_ue_grade")
    private String oldUeGrade;

    @Column(name = "old_uc_grade")
    private String oldUcGrade;

    @Column(name = "old_at_grade")
    private String oldAtGrade;
    @Column(name = "new_name")
    private String newName;

    @Column(name = "new_cname")
    private String newCname;

    @Column(name = "new_hkid")
    private String newHkid;

    @Column(name = "new_passport")
    private String newPassport;

    @Column(name = "new_email")
    private String newEmail;

    @Column(name = "new_bl_grade")
    private String newBlGrade;

    @Column(name = "new_ue_grade")
    private String newUeGrade;

    @Column(name = "new_uc_grade")
    private String newUcGrade;

    @Column(name = "new_at_grade")
    private String newAtGrade;

    @Column(name = "remark")
    private String remark;

    @Column(name = "certStage")
    @Enumerated(EnumType.STRING)
    private CertStage certStage;

    @Column(name = "status")
    private String status;

    @Column(name = "done")
    private Boolean done;

    @Column(name = "letter_type")
    private String letterType;
}
