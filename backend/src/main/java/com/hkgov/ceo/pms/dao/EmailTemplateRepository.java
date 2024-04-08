package com.hkgov.ceo.pms.dao;

import com.hkgov.ceo.pms.entity.EmailTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, Long> {

    @Query("select e from EmailTemplate e where e.templateCode = :emailTemplateCode")
    Optional<EmailTemplate> findByEmailTemplateCode(String emailTemplateCode);
}
