package com.hkgov.csb.eproof.dao;

import com.hkgov.csb.eproof.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<File,Long> {

    @Query(nativeQuery = true,value = """
    SELECT * 
    FROM file f 
    WHERE f.id IN 
          (SELECT p.file_id FROM cert_pdf p INNER JOIN cert_info c on p.cert_info_id=c.id WHERE c.id = :certInfoId)
    ORDER BY f.created_date DESC
    LIMIT 1
""")
    File getLatestPdfForCert(Long certInfoId);
    @Query(nativeQuery = true,value = """
    SELECT * 
    FROM file f 
    WHERE f.id IN 
          (SELECT p.file_id FROM cert_renew_pdf p INNER JOIN cert_info_renew c on p.cert_info_renew_id=c.id WHERE c.id = :certInfoId)
    ORDER BY f.created_date DESC
    LIMIT 1
""")
    File getLatestPdfForCertRenewId(Long certInfoId);

}
