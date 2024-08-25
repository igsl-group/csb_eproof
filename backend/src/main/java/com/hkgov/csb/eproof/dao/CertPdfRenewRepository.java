package com.hkgov.csb.eproof.dao;

import com.hkgov.csb.eproof.entity.CertPdfRenew;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertPdfRenewRepository extends JpaRepository<CertPdfRenew,Long> {
    CertPdfRenew findByCertInfoRenewId(Long id);
}
