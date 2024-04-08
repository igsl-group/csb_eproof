package com.hkgov.ceo.pms.dao;

import com.hkgov.ceo.pms.entity.Role;
import com.hkgov.ceo.pms.entity.User;
import com.hkgov.ceo.pms.entity.UserHasRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserHasRoleRepository extends JpaRepository<UserHasRole, Long> {
    @Query("select u from UserHasRole u where u.user = :user and u.role = :role")
    UserHasRole findByUserAndRole(@Param("user") User user, @Param("role") Role role);

}
