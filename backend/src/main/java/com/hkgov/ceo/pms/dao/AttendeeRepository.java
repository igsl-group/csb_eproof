package com.hkgov.ceo.pms.dao;

import com.hkgov.ceo.pms.entity.Attendee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttendeeRepository extends JpaRepository<Attendee, Long> {
}
