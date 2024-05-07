package com.hkgov.csb.eproof.dao;

import com.hkgov.csb.eproof.entity.Password;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordRepository extends JpaRepository<Password, Long> {
}
