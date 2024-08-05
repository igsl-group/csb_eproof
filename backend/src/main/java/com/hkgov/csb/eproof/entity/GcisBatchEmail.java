package com.hkgov.csb.eproof.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "gcis_batch_email")
@Getter
@Setter
public class GcisBatchEmail extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "email_template_id")
    private Long emailTemplateId;

    @Column(name = "schedule_datetime")
    private LocalDateTime scheduleDatetime;

    @Column(name = "xml", columnDefinition = "text")
    private String xml;

    @Column(name = "status")
    private String status;

}