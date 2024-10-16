package com.hkgov.csb.eproof.dao;

import com.hkgov.csb.eproof.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File,Long> {

    @Query(nativeQuery = true,value = """
    SELECT * 
    FROM file f 
    WHERE f.id IN 
          (SELECT p.file_id FROM cert_pdf_renew p INNER JOIN cert_info_renew c on p.cert_info_renew_id=c.id WHERE c.id = :certInfoRenewId)
    ORDER BY f.created_date DESC
    LIMIT 1
""")
    File getLatestPdfForCertRenew(Long certInfoRenewId);



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
    SELECT f.* 
    FROM file f left join cert_pdf c on f.id = c.file_id
    WHERE c.cert_info_id IN :ids
""")
    List<File> getLatestPdfForCerts(List<Long> ids);


    @Query(value = """
      SELECT f FROM File f
      JOIN CertPdf cp on f.id = cp.file.id
      JOIN cp.certInfo ci
      WHERE ci.id IN :ids
      AND f.createdDate =
      (SELECT MAX(f2.createdDate) 
      FROM File f2 LEFT JOIN CertPdf cp2 ON f2.id = cp2.file.id LEFT JOIN cp2.certInfo ci2 WHERE ci2.id = ci.id)
    """)
   /* @Query(nativeQuery = true,value = """
    select f.* from file f
        where f.created_date =  (
        select MAX(f2.created_date) from file f2 left join cert_pdf cp on f2.id = cp.file_id
                                                 left join cert_info ci on cp.cert_info_id = ci.id
                                                 where ci.id
      )
    """)*/
    List<File> getLatestPdfForCerts2(List<Long> ids);



}
