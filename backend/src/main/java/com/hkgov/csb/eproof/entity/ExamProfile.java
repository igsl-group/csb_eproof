package com.hkgov.csb.eproof.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "exam_profile")
@Getter
@Setter

public class ExamProfile extends BaseEntity{

    @Id
    @Column(name = "serial_no")
    private String serialNo;

    @Column(name = "exam_date")
    private LocalDate examDate;

    @Column(name = "announce_date")
    private LocalDate announceDate;

    @Column(name = "location")
    private String location;

    @Column(name = "status")
    private String status;

    @Column(name = "is_freezed")
    private Boolean isFreezed;

}