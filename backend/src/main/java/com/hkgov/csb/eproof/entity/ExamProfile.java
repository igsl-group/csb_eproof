package com.hkgov.csb.eproof.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "exam_profile")
@Data
public class ExamProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "client_ip_address")
    private String clientIpAddress;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "action")
    private String action;

    @Column(name = "resource")
    private String resource;

}