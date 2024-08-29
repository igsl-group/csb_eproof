package com.hkgov.csb.eproof.dao;

import com.hkgov.csb.eproof.entity.CertAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CertActionRepository extends JpaRepository<CertAction,Long> {
    @Query("SELECT ca FROM CertAction ca where ca.approver = :userId")
    List<CertAction> findByUser(Long userId);

    @Query("SELECT ca FROM CertAction ca where ca.status = 'Pending' or ca.status = 'Rejected'")
    List<CertAction> findPendingOrRejected();

    @Query("select c from CertAction c where c.status <> 'APPROVED' and c.type = 'REVOKE' and  c.hkid = :hkid ")
    List<CertAction> getinfoByHkid(String hkid);

    @Query("select c from CertAction c where c.status <> 'APPROVED' and c.type = 'REVOKE' and  c.passportNo = :passport ")
    List<CertAction> getinfoByPassport(String passport);
}
