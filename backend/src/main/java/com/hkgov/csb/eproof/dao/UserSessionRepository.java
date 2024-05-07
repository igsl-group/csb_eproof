package com.hkgov.csb.eproof.dao;

import com.hkgov.csb.eproof.entity.User;
import com.hkgov.csb.eproof.entity.UserSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    @Query("select u from UserSession u where TIMESTAMPDIFF(SECOND, u.timeStamp, CURRENT_TIMESTAMP) < :expirationSeconds and (:keyword is null or u.user.name like %:keyword%)")
    Page<UserSession> findByUser(@Param("expirationSeconds") Integer expirationSeconds, Pageable pageable, @Param("keyword") String keyword);

    @Query("select us from UserSession us where TIMESTAMPDIFF(SECOND, us.timeStamp, CURRENT_TIMESTAMP) < :expirationSeconds")
    Page<UserSession> findAllValidUserSession(@Param("expirationSeconds") Integer expirationSeconds, Pageable pageable);


    @Transactional
    @Modifying
    @Query("delete from UserSession us where us.user = :user")
    void deleteAllByUser(User user);

    @Transactional
    @Modifying
    @Query("delete from UserSession us where TIMESTAMPDIFF(SECOND, us.timeStamp, CURRENT_TIMESTAMP) >= :expirationSeconds")
    void deleteAllExpiredUserSession(@Param("expirationSeconds") Integer expirationSeconds);
}