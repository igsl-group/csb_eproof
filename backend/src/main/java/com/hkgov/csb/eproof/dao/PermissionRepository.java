package com.hkgov.csb.eproof.dao;

import com.hkgov.csb.eproof.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PermissionRepository extends JpaRepository<Permission,Long> {
    @Query("select p from RoleHasPermission r left join Permission p on r.permissionId = p.id where r.roleId = :id")
    List<Permission> getRoleByRoleId(@Param("id") Long id);

    @Query("select p from Permission p order by p.code")
    List<Permission> defaultSelectAllWithOrdering();
}
