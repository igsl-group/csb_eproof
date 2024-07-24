package com.hkgov.csb.eproof.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "audit_log")
@Getter
@Setter
public class AuditLog extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "ip_address")
    private String ipAddress;
    @Column(name = "computer_information")
    private String computerInformation;
    @Column(name = "log_details",columnDefinition = "varchar(1000)")
    private String logDetails;
    @Column(name = "log_action")
    private String logAction;
    @Column(name = "request_body" ,columnDefinition="TEXT")
    private String requestBody;
}
