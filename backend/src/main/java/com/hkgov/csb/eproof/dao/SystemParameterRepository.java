package com.hkgov.csb.eproof.dao;

import com.hkgov.csb.eproof.entity.SystemParameter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SystemParameterRepository extends JpaRepository<SystemParameter,Long> {

    Optional<SystemParameter> findByName(String name);

    @Query("select u from SystemParameter u where:keyword is null or (u.name like %:keyword%)")
    Page<SystemParameter> findPage(Pageable pageable, @Param("keyword") String keyWord);

}
