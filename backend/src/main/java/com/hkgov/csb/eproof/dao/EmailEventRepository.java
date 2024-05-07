package com.hkgov.csb.eproof.dao;

import com.hkgov.csb.eproof.entity.EmailEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailEventRepository extends JpaRepository<EmailEvent, Long> {
}
