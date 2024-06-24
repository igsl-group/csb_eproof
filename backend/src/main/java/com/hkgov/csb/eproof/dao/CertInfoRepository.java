package com.hkgov.csb.eproof.dao;

import com.hkgov.csb.eproof.dto.CertSearchDto;
import com.hkgov.csb.eproof.entity.CertInfo;
import com.hkgov.csb.eproof.entity.enums.CertStage;
import com.hkgov.csb.eproof.entity.enums.CertStatus;
import org.jetbrains.annotations.NotNull;
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
    @Query(value = """
select c from CertInfo c
        left join fetch c.examProfile
        left join fetch c.pdfList
        where c.examProfileSerialNo = :serialNo AND c.certStage = :stage AND c.certStatus in :statusList
""")
    List<CertInfo> getCertByExamSerialAndStageAndStatus(String serialNo,
                                                        CertStage stage,
                                                        List<CertStatus> statusList);
    @Query(nativeQuery = true,value = """
        SELECT * FROM cert_info c WHERE
        (
            (c.cert_stage IN :certStageList) AND
            (c.status IN :certStatusList)
        )
        AND
        (
            ( ?#{#searchDto.hkid} IS NOT null AND c.hkid like %?#{#searchDto.hkid}% ) OR
            ( ?#{#searchDto.passportNo} IS NOT null AND c.passport_no like %?#{#searchDto.passportNo}% ) OR
            ( ?#{#searchDto.canName} IS NOT null AND c.name like %?#{#searchDto.canName}% ) OR
            ( ?#{#searchDto.canCName} IS NOT null AND c.cname like %?#{#searchDto.canCName}% ) OR
            ( ?#{#searchDto.canEmail} IS NOT null AND c.email like %?#{#searchDto.canEmail}% ) OR
            ( ?#{#searchDto.examProfileSerialNo} IS NOT null AND c.exam_profile_serial like %?#{#searchDto.examProfileSerialNo}% ) OR
            ( ?#{#searchDto.blnstGrade} IS NOT null AND c.blnst_grade like %?#{#searchDto.blnstGrade}% ) OR
            ( ?#{#searchDto.ueGrade} IS NOT null AND c.ue_grade like %?#{#searchDto.ueGrade}% ) OR
            ( ?#{#searchDto.ucGrade} IS NOT null AND c.uc_grade like %?#{#searchDto.ucGrade}% ) OR
            ( ?#{#searchDto.atGrade} IS NOT null AND c.at_grade like %?#{#searchDto.atGrade}% ) OR
            ( ?#{#searchDto.certValid} IS NOT null AND c.is_valid = ?#{#searchDto.certValid} )
        )
""")
    Page<CertInfo> certSearch(@Param("searchDto") CertSearchDto searchDto,
                              @Param("certStageList") @NotNull List<String> certStageList,
                              @Param("certStatusList") @NotNull List<String> certStatusList,
                              Pageable pageable);

    @Query("SELECT c FROM CertInfo c WHERE c.id in :certInfoIdList")
    List<CertInfo> getByIdIn(List<Long> certInfoIdList);

    @Query("select c from CertInfo c left join ExamProfile where c.examProfileSerialNo = :serialNo")
    List<CertInfo> getInfoListByExamSerialNo(@Param("serialNo") String serialNo);

    @Query("select c from CertInfo c left join ExamProfile where c.examProfileSerialNo = :serialNo and c.certStage= :stage and c.certStatus = 'SUCCESS' and c.onHold = false  ")
    List<CertInfo> getinfoByNoAndStatus(@Param("serialNo") String serialNo,@Param("stage") CertStage stage);


    @Query("""
    SELECT COUNT(c) FROM CertInfo c
    WHERE (:certStatus != null AND c.certStatus = :certStatus AND c.examProfileSerialNo = :examProfileSerialNo and c.certStage = :certStage)
    OR (:certStatus = null AND c.examProfileSerialNo = :examProfileSerialNo and c.certStage = :certStage)    
""")
    Integer countByStageAndStatus(String examProfileSerialNo, CertStage certStage, CertStatus certStatus);

    @Query("select c from CertInfo c where c.hkid = :hkid")
    List<CertInfo> findAllByHkid(@Param("hkid") String hkid);


    @Query("select c from CertInfo c where c.passportNo = :passportNo")
    List<CertInfo> findAllByPassport(@Param("passportNo") String passportNo);

}
