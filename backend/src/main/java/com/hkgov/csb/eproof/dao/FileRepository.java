package com.hkgov.csb.eproof.dao;

import com.hkgov.csb.eproof.entity.File;
import com.hkgov.csb.eproof.entity.LetterTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<File,Long> {

}
