package com.hkgov.csb.eproof.config;

import com.hkgov.csb.eproof.audit.common.AuditorAwareImpl;
import com.hkgov.csb.eproof.dao.AuditorDetailsRepository;
import com.hkgov.csb.eproof.entity.AuditorDetails;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class PersistenceConfig {

    private final AuditorDetailsRepository auditorDetailsRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public PersistenceConfig(AuditorDetailsRepository auditorDetailsRepository) {
        this.auditorDetailsRepository = auditorDetailsRepository;
    }

    @Bean
    AuditorAware<AuditorDetails> auditorProvider() {
        return new AuditorAwareImpl(auditorDetailsRepository, entityManager);
    }
}
