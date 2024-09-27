package com.hkgov.csb.eproof.dao;

import com.hkgov.csb.eproof.entity.EmailMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailMessageRepository extends JpaRepository<EmailMessage,Long> {
    @Query("select e from EmailMessage e where:keyword is null or (e.subject like %:keyword% or e.body like %:keyword% " +
            "or e.to like %:keyword% or e.type like %:keyword% or e.cc like %:keyword% or e.bcc like %:keyword%)")
    Page<EmailMessage> findPage(Pageable pageable, @Param("keyword") String keyWord);
}
