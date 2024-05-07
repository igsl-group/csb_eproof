package com.hkgov.csb.eproof.dao;

import com.hkgov.csb.eproof.entity.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ConfigurationRepository extends JpaRepository<Configuration, Long> {

    @Query("select c from Configuration c where c.code = :code")
    Configuration findByCode(@Param("code") String code);

}