package com.hkgov.csb.eproof.dao;

import com.hkgov.csb.eproof.dto.CertSearchDto;
import com.hkgov.csb.eproof.entity.CertInfo;
import com.hkgov.csb.eproof.entity.enums.CertStage;
import com.hkgov.csb.eproof.entity.enums.CertStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CertInfoRepository extends JpaRepository<CertInfo,Long> {
    @Query("select c from CertInfo c where c.examProfileSerialNo = :serialNo")
    CertInfo getInfoByNo(@Param("serialNo") String serialNo);
    List<CertInfo> getInfoByExamProfileSerialNo(@Param("serialNo") String serialNo);
    @Query(value = "select c from CertInfo c where c.examProfileSerialNo = :serialNo AND c.certStage = :stage AND c.certStatus in :statusList")
    List<CertInfo> getCertByExamSerialAndStageAndStatus(String serialNo,
                                                        CertStage stage,
                                                        List<CertStatus> statusList);
    @Query("""
        SELECT c FROM CertInfo c WHERE
        (
            (:caseStageList != null AND c.certStage IN :caseStageList) AND
            (:caseStatusList != null AND c.certStatus IN :caseStatusList)
        )
        AND
        (
            ( ?#{#searchDto.certValid} != null AND c.valid = ?#{#searchDto.certValid} ) OR
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

    @Query("select c from CertInfo c left join ExamProfile where c.examProfileSerialNo = :serialNo")
    List<CertInfo> getinfoByNoList(@Param("serialNo") String serialNo);

    @Query("select c from CertInfo c left join ExamProfile where c.examProfileSerialNo = :serialNo and c.certStage= :stage and c.certStatus = 'pending' and c.onHold = false  ")
    List<CertInfo> getinfoByNoAndStatus(@Param("serialNo") String serialNo,@Param("stage") CertStage stage);


    @Query("""
    SELECT COUNT(c) FROM CertInfo c
    WHERE (:certStatus != null AND c.certStatus = :certStatus AND c.examProfileSerialNo = :examProfileSerialNo and c.certStage = :certStage)
    OR (:certStatus = null AND c.examProfileSerialNo = :examProfileSerialNo and c.certStage = :certStage)    
""")
    Integer countByStageAndStatus(String examProfileSerialNo, CertStage certStage, CertStatus certStatus);
}
