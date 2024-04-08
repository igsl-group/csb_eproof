package com.hkgov.ceo.pms.dao;

import com.hkgov.ceo.pms.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    @Query("select p from PasswordResetToken p where p.token = :token")
    PasswordResetToken findByToken(@Param("token") String token);

}
