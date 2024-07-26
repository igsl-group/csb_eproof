package com.hkgov.csb.eproof.dao;

import com.hkgov.csb.eproof.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog,Long> {

    @Query("select u from AuditLog u where:keyword is null or (u.createdBy like %:keyword% or u.ipAddress like %:keyword% or u.logDetails like %:keyword% or u.requestBody like %:keyword%)")
    Page<AuditLog> findPage(Pageable pageable, @Param("keyword") String keyWord);
}
