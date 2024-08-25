package com.hkgov.csb.eproof.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "cert_pdf_renew")
@Getter
@Setter
public class CertPdfRenew extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "cert_info_renew_id")
    private Long certInfoRenewId;

    @Column(name = "file_id")
    private Long fileId;

    // Mapped tables
    @ManyToOne(fetch = FetchType.LAZY)
    @Setter(AccessLevel.NONE)
    @JoinColumn(name = "cert_info_renew_id", insertable = false, updatable = false)
    private CertInfoRenew certInfoRenew;

    // Mapped tables
    @ManyToOne(fetch = FetchType.LAZY)
    @Setter(AccessLevel.NONE)
    @JoinColumn(name = "file_id", insertable = false, updatable = false)
    private File file;
}
