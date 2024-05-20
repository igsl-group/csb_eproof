package com.hkgov.csb.eproof.dao;

import com.hkgov.csb.eproof.dto.CertSearchDto;
import com.hkgov.csb.eproof.entity.CertInfo;
import com.hkgov.csb.eproof.entity.ExamProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CertInfoRepository extends JpaRepository<CertInfo,Long> {
    @Query("select c from CertInfo c where c.examProfile = :serialNo")
    CertInfo getinfoByNo(@Param("serialNo") ExamProfile serialNo);


}
