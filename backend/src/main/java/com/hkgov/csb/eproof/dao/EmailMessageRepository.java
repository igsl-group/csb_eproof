package com.hkgov.csb.eproof.dao;

import com.hkgov.csb.eproof.entity.EmailMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailMessageRepository extends JpaRepository<EmailMessage,Long> {
}
