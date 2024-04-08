package com.hkgov.ceo.pms.config;

import com.hkgov.ceo.pms.audit.common.AuditorAwareImpl;
import com.hkgov.ceo.pms.dao.AuditorDetailsRepository;
import com.hkgov.ceo.pms.entity.AuditorDetails;
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
