package com.hkgov.csb.eproof.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hkgov.csb.eproof.constants.Constants;
import com.hkgov.csb.eproof.entity.enums.CertStage;
import com.hkgov.csb.eproof.entity.enums.CertStatus;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "cert_pdf")
@Getter
@Setter
public class CertPdf extends BaseEntity{

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
    @ManyToOne
    @Setter(AccessLevel.NONE)
    @JoinColumn(name = "cert_info_id", insertable = false, updatable = false)
    private File file;
}