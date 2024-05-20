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


    @Query("""
        SELECT c FROM CertInfo c WHERE
        (
            (:caseStageList != null AND c.certStage IN :caseStageList) AND
            (:caseStatusList != null AND c.certStatus IN :caseStatusList)
        )
        AND
        (
            ( ?#{#searchDto.hkid} != null AND c.hkid like %?#{#searchDto.hkid}% ) OR
            ( ?#{#searchDto.passportNo} != null AND c.passportNo like %?#{#searchDto.passportNo}% ) OR
            ( ?#{#searchDto.canName} != null AND c.name like %?#{#searchDto.canName}% ) OR
            ( ?#{#searchDto.canCName} != null AND c.cname like %?#{#searchDto.canCName}% ) OR
            ( ?#{#searchDto.canEmail} != null AND c.email like %?#{#searchDto.canEmail}% ) OR
            ( ?#{#searchDto.examProfileSerialNo} != null AND c.examProfile.serialNo like %?#{#searchDto.examProfileSerialNo}% ) OR
            
            ( ?#{#searchDto.blnstGrade} != null AND c.blnstGrade like %?#{#searchDto.blnstGrade}% ) OR
            ( ?#{#searchDto.ueGrade} != null AND c.ueGrade like %?#{#searchDto.ueGrade}% ) OR
            ( ?#{#searchDto.ucGrade} != null AND c.ucGrade like %?#{#searchDto.ucGrade}% ) OR
            ( ?#{#searchDto.atGrade} != null AND c.atGrade like %?#{#searchDto.atGrade}% )
        )
""")
    Page<CertInfo> caseSearch(@Param("searchDto") CertSearchDto searchDto,
                              @Param("caseStageList") List<String> certStageList,
                              @Param("caseStatusList") List<String> certStatusList,
                              Pageable pageable);
}
