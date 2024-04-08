package com.hkgov.ceo.pms.dao;

import com.hkgov.ceo.pms.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoleRepository extends JpaRepository<Role, Long> {
    @Query("select r from Role r where r.code = :code")
    Role findByCode(@Param("code") String code);

    @Query("select r from Role r where :keyword is null or (r.code like %:keyword% or r.name like %:keyword%)")
    Page<Role> findByCodeOrName(Pageable pageable, @Param("keyword") String keyword);
}
