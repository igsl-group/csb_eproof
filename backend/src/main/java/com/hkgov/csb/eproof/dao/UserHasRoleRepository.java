package com.hkgov.csb.eproof.dao;

import com.hkgov.csb.eproof.entity.UserHasRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserHasRoleRepository extends JpaRepository<UserHasRole, Long> {
    @Query("select u from UserHasRole u where u.userId = :id ")
    List<UserHasRole> roles(@Param("id") Long id);
}
