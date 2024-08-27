package com.hkgov.csb.eproof.dao;

import com.hkgov.csb.eproof.entity.CertPdf;
import com.hkgov.csb.eproof.entity.CertPdfRenew;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CertPdfRenewRepository extends JpaRepository<CertPdfRenew,Long> {
    List<CertPdfRenew> findByCertInfoRenewId(Long id);

    @Query("SELECT c FROM CertPdfRenew c WHERE c.certInfoRenewId = :certInfoRenewId ORDER BY c.modifiedDate DESC")
    CertPdfRenew getLatestCertPdf(Long certInfoRenewId);

}
