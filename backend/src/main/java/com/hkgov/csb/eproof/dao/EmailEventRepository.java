package com.hkgov.csb.eproof.dao;


import com.hkgov.csb.eproof.entity.EmailEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmailEventRepository extends JpaRepository<EmailEvent, Long> {

    @Query("""
        SELECT e FROM EmailEvent e 
        WHERE e.status = 'PENDING' 
        AND e.scheduleDatetime <= current_timestamp
        """)

    List<EmailEvent> findPendingEmailEvent();
}
