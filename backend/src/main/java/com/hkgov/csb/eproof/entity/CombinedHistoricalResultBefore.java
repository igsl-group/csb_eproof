package com.hkgov.csb.eproof.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hkgov.csb.eproof.constants.Constants;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "combined_historical_result_before_2024")
@Getter
@Setter
public class CombinedHistoricalResultBefore extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "exam_date")
    @JsonFormat(pattern = Constants.DATE_PATTERN)
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

    @Column(name = "bl_date")
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate blDate;

    @Column(name = "bl_void")
    private Boolean blVoid;

    @Column(name = "ue_grade")
    private String ueGrade;

    @Column(name = "ue_date")
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate ueDate;

    @Column(name = "ue_void")
    private Boolean ueVoid;

    @Column(name = "uc_grade")
    private String ucGrade;

    @Column(name = "uc_date")
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate ucDate;

    @Column(name = "uc_void")
    private Boolean ucVoid;

    @Column(name = "at_grade")
    private String atGrade;

    @Column(name = "at_date")
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate atDate;

    @Column(name = "at_void")
    private Boolean atVoid;

    @Column(name = "remark")
    private String remark;

    @Column(name = "valid")
    private Boolean valid;

    @Column(name = "action_freeze")
    private Boolean actionFreeze;

    @OneToMany(mappedBy = "historicalResult", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<CombinedHisResultBefApprove> historicalResultBefApproves;
}
