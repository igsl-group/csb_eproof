package com.hkgov.ceo.pms.dao;

import com.hkgov.ceo.pms.entity.RoleHasPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoleHasPermissionRepository extends JpaRepository<RoleHasPermission, Long> {
    @Query("select r from RoleHasPermission r where r.role.code = :roleCode and r.permission.code = :permissionCode")
    RoleHasPermission findByRoleCodeAndPermissionCode(@Param("roleCode") String roleCode, @Param("permissionCode") String permissionCode);
}
