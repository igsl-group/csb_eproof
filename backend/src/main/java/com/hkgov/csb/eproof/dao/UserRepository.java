package com.hkgov.csb.eproof.dao;

import com.hkgov.csb.eproof.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select u from User u where u.dpUserId = :dpUserId and u.dpDeptId = :dpDeptId and u.status = 'Active'")
    Optional<User> findByUserIdAndDeptId(@Param("dpUserId") String dpUserId, @Param("dpDeptId") String dpDeptId);

    @Query("select u from User u where u.id = :dpUserId and u.dpDeptId = :dpDeptId")
    User getUserByDpUserIdAndDpDeptId(@Param("dpUserId") String dpUserId, @Param("dpDeptId") String dpDeptId);

    @Query("select u from User u where u.id = :dpUserId ")
    User getUserBydpUserId(@Param("dpUserId") String dpUserId);

    @Query("select u from User u where u.dpUserId = :id")
    User getUserById(@Param("id") String id);
}
