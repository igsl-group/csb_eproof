package com.hkgov.csb.eproof.dao;

import com.hkgov.csb.eproof.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long> {

    @Query("select r from Role r where :keyword is null or (r.name like %:keyword% )")
    Page<Role> findByCodeOrName(Pageable pageable, @Param("keyword") String keyword);
    @Query("select r from Role r where r.id = :id")
    List<Role> getRoleById(@Param("id") Long id);

    @Query("select r from UserHasRole u left join Role r on u.roleId=r.id where u.userId = :id ")
    List<Role> roles(@Param("id") Long id);
}
