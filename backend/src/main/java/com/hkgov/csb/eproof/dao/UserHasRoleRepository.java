package com.hkgov.csb.eproof.dao;

import com.hkgov.csb.eproof.entity.Role;
import com.hkgov.csb.eproof.entity.User;
import com.hkgov.csb.eproof.entity.UserHasRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserHasRoleRepository extends JpaRepository<UserHasRole, Long> {
    @Query("select u from UserHasRole u where u.user = :user and u.role = :role")
    UserHasRole findByUserAndRole(@Param("user") User user, @Param("role") Role role);

}
