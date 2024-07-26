package com.hkgov.csb.eproof.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "email_template")
@Getter
@Setter
public class EmailTemplate extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "template_name")
    private String templateName;

    @Column(name = "subject",columnDefinition = "varchar(1000)")
    private String subject;

    @Column(name = "body",columnDefinition = "text")
    private String body;

    @Column(name = "type")
    private String type;

    @Column(name = "include_emails")
    private String includeEmails;
}