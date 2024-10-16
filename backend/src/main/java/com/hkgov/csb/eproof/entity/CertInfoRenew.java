package com.hkgov.csb.eproof.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.hkgov.csb.eproof.entity.enums.CertStage;
import com.hkgov.csb.eproof.entity.enums.CertStatus;
import com.hkgov.csb.eproof.entity.enums.CertType;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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

//    @Column(name = "old_cname")
//    private String oldCname;

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

/*    @Column(name = "new_cname")
    private String newCname;*/

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
    @Enumerated(EnumType.STRING)
    private CertStatus certStatus;

    @Column(name = "done")
    private Boolean done;

    @Column(name = "old_letter_type")
    private String oldLetterType;

    @Column(name = "new_letter_type")
    private String newLetterType;

    @Column(name = "is_delete")
    private Boolean isDelete;

    @ManyToOne
    @JoinColumn(name = "cert_info_id", insertable = false, updatable = false)
    @JsonBackReference
    private CertInfo certInfo;

    @OneToOne(mappedBy = "certInfoRenew", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private CertEproofRenew certEproofRenew;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinTable(
            name = "cert_pdf_renew",
            joinColumns = @JoinColumn(name = "cert_info_renew_id"),
            inverseJoinColumns = @JoinColumn(name = "file_id")
    )
    private List<File> pdfList;

    //Getter for JSON for docx merging

    public String getName() {
        return newName;
    }

    public String getBlnstGrade() {
        return newBlGrade;
    }

    public String getUeGrade() {
        return newUeGrade;
    }

    public String getUcGrade() {
        return newUcGrade;
    }

    public String getAtGrade() {
        return newAtGrade;
    }

    public String getEmail() {
        return newEmail;
    }

    public String getHkidOrPassport(){
        if (StringUtils.isNotEmpty(newHkid)){
            return newHkid;
        } else{
            return newPassport;
        }
    }
}
