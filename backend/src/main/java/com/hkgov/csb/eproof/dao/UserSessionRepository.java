package com.hkgov.csb.eproof.dao;

import com.hkgov.csb.eproof.entity.User;
import com.hkgov.csb.eproof.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    @Query("select us from UserSession us where us.user.id = :userId")
    UserSession getUserSessionByUserId(@Param("userId") Long userId);

    @Query("select us from UserSession us where us.jwt = :jwt")
    UserSession getUserSessionByJwt(@Param("jwt") String jwt);
}
