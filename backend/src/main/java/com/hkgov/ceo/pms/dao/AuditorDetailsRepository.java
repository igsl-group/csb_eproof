package com.hkgov.ceo.pms.dao;

import com.hkgov.ceo.pms.entity.AuditorDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuditorDetailsRepository extends JpaRepository<AuditorDetails, Long> {
    @Query("""
            select a from AuditorDetails a
            where a.userId = :userId
            and a.userName = :userName
            and a.post = :post
            and a.hostname = :hostname
            """)
    AuditorDetails findByUserIdAndUserNameAAndHostAndHostname(@Param("userId") String userId,
                                                              @Param("userName") String userName,
                                                              @Param("post") String post,
                                                              @Param("hostname") String hostname);

    @Query("select a from AuditorDetails a where a.userId = :userId")
    AuditorDetails findByUserId(@Param("userId") String userId);
}