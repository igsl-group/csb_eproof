package com.hkgov.csb.eproof.dao;

import com.hkgov.csb.eproof.entity.SystemParameter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SystemParameterRepository extends JpaRepository<SystemParameter,Long> {

    Optional<SystemParameter> findByName(String name);

}
