package com.hkgov.csb.eproof.dao;

import com.hkgov.csb.eproof.entity.LetterTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LetterTemplateRepository extends JpaRepository<LetterTemplate,Long> {

    @Query("SELECT l from LetterTemplate l where l.name = :name")
    LetterTemplate getByName(String name);
}
