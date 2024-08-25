package com.hkgov.csb.eproof.dao;

import com.hkgov.csb.eproof.entity.CertEproofRenew;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertEproofRenewRepository extends JpaRepository<CertEproofRenew, Long> {
    CertEproofRenew findByCertInfoRenewId(Long certInfoRenewId);
}