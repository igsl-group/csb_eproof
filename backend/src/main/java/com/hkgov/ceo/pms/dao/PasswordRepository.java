package com.hkgov.ceo.pms.dao;

import com.hkgov.ceo.pms.entity.Password;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordRepository extends JpaRepository<Password, Long> {
}
