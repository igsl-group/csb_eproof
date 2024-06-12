package com.hkgov.csb.eproof.dao;


import com.hkgov.csb.eproof.entity.RoleHasPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoleHasPermissionRepository extends JpaRepository<RoleHasPermission,Long> {
    @Query("select r from RoleHasPermission r where r.roleId = :roleId")
    List<RoleHasPermission> getAllByRoleId(@Param("roleId") Long roleId);

}
