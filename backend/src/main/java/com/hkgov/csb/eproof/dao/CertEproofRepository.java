package com.hkgov.csb.eproof.dao;

import com.hkgov.csb.eproof.entity.CertEproof;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CertEproofRepository extends JpaRepository<CertEproof,Long> {

    @Modifying
    @Query("delete from CertEproof where certInfoId = :certInfoId")
    void deleteExistingRecord(@Param("certInfoId") Long certInfoId);

    @Query("SELECT ce FROM CertEproof ce where ce.certInfoId = :certInfoId order by ce.version desc")
    CertEproof findByCertInfoId(Long certInfoId);

    @Query("SELECT ce FROM CertEproof ce where ce.uuid = :uuid and ce.version = :version")
    CertEproof findByUuidAndVersion(String uuid, Integer version);

    @Query("SELECT c.path FROM CertEproof a left join CertPdf b on a.certInfoId=b.certInfoId left join File c on c.id=b.fileId where a.uuid = :uuid and a.version = :version")
    String getPath(String uuid,Integer version);
}
