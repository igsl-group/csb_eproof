package com.hkgov.csb.eproof.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "cert_pdf")
@Getter
@Setter
public class CertPdf extends BaseEntity implements Cloneable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "cert_info_id")
    private Long certInfoId;

    @Column(name = "file_id")
    private Long fileId;

    // Mapped tables
    @ManyToOne
    @Setter(AccessLevel.NONE)
    @JoinColumn(name = "cert_info_id", insertable = false, updatable = false)
    private CertInfo certInfo;

    // Mapped tables
    @OneToOne
    @Setter(AccessLevel.NONE)
    @JoinColumn(name = "file_id", insertable = false, updatable = false)
    private File file;

    @Override
    public CertPdf clone() {
        try {
            return (CertPdf) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Cloning failed", e);
        }
    }
}