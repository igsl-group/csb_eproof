package com.hkgov.ceo.pms.dao;

import com.hkgov.ceo.pms.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    @Query("select p from Permission p where p.code = :code")
    Permission findByCode(@Param("code") String code);

}
