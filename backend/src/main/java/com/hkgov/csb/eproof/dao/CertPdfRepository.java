package com.hkgov.csb.eproof.dao;

import com.hkgov.csb.eproof.entity.CertInfo;
import com.hkgov.csb.eproof.entity.CertPdf;
import com.hkgov.csb.eproof.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CertPdfRepository extends JpaRepository<CertPdf,Long> {
    List<CertPdf> findByCertInfoId(Long certInfoId);
}
