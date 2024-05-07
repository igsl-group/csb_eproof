package com.hkgov.csb.eproof.dao;

import com.hkgov.csb.eproof.entity.EmailTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, Long> {

    @Query("select e from EmailTemplate e where e.templateCode = :emailTemplateCode")
    Optional<EmailTemplate> findByEmailTemplateCode(String emailTemplateCode);
}
