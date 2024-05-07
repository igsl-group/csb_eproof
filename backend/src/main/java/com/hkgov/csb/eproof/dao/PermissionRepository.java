package com.hkgov.csb.eproof.dao;

import com.hkgov.csb.eproof.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    @Query("select p from Permission p where p.code = :code")
    Permission findByCode(@Param("code") String code);

}
