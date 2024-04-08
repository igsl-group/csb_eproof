package com.hkgov.ceo.pms.dao;

import com.hkgov.ceo.pms.entity.EmailEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailEventRepository extends JpaRepository<EmailEvent, Long> {
}
