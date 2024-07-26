package com.hkgov.csb.eproof.dao;

import com.hkgov.csb.eproof.entity.CertEproof;
import com.hkgov.csb.eproof.entity.CertInfoRenew;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CertEproofRepository extends JpaRepository<CertEproof,Long> {


    @Query("SELECT ce FROM CertEproof ce where ce.certInfoId = :certInfoId order by ce.version desc")
    List<CertEproof> findByCertInfoId(Long certInfoId);
}
