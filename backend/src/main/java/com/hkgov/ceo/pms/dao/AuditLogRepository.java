package com.hkgov.ceo.pms.dao;

import com.hkgov.ceo.pms.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    Page<AuditLog> findAll(Pageable pageable);

    @Query("select a from AuditLog a where :keyword is null or (a.principal like %:keyword% or a.action like %:keyword% or a.resource like %:keyword%)")
    Page<AuditLog> findByPrincipalOrActionOrResource(Pageable pageable, @Param("keyword") String keyword);

    @Transactional
    @Modifying
    @Query("delete from AuditLog a where ADDDATE(a.actionDateTime, :auditRetentionDays) < CURRENT_TIMESTAMP")
    int purgeAuditLogByRetentionDays(@NonNull @Param("auditRetentionDays") int auditRetentionDays);

    @Query(value = """
            select * from audit_log as a
            order by a.action_date_time desc
            limit :auditLogMaxNo, 18446744073709551615
            """, nativeQuery = true)
    List<AuditLog> findAuditLogsByMaxNo(@NonNull @Param("auditLogMaxNo") int auditLogMaxNo);

    @Query(value = """
            select *
            from audit_log
            where action_date_time >= TIMESTAMP(CONCAT(:from , ' 00:00:00'))
              and action_date_time <= TIMESTAMP(CONCAT(:to , ' 23:59:59'));
            """, nativeQuery = true)
    List<AuditLog> findAuditLogsByActionDateTime(@Param("from") LocalDate from, @Param("to") LocalDate to);
}