package com.hkgov.csb.eproof.dao;

import com.hkgov.csb.eproof.entity.EmailTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailTemplateRepository  extends JpaRepository<EmailTemplate,Long> {
    @Query("select u from EmailTemplate u where:keyword is null or (u.emailKey like %:keyword%)")
    Page<EmailTemplate> findPage(Pageable pageable, @Param("keyword") String keyWord);
}
