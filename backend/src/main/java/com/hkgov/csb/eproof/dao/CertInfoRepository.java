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
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CertInfoRepository extends JpaRepository<CertInfo,Long> {
    @Query("select c from CertInfo c where c.examProfileSerialNo = :serialNo")
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
            ( ?#{#searchDto.hkid} IS null OR c.hkid like %?#{#searchDto.hkid}% ) AND
            ( ?#{#searchDto.passportNo} IS null OR c.passport_no like %?#{#searchDto.passportNo}% ) AND
            ( ?#{#searchDto.canName} IS null OR c.name like %?#{#searchDto.canName}% ) AND
            ( ?#{#searchDto.canCName} IS null OR c.cname like %?#{#searchDto.canCName}% ) AND
            ( ?#{#searchDto.canEmail} IS null OR c.email like %?#{#searchDto.canEmail}% ) AND
            ( ?#{#searchDto.examProfileSerialNo} IS null OR c.exam_profile_serial = ?#{#searchDto.examProfileSerialNo} ) AND
            ( ?#{#searchDto.blnstGrade} IS null OR c.blnst_grade like %?#{#searchDto.blnstGrade}% ) AND
            ( ?#{#searchDto.ueGrade} IS null OR c.ue_grade like %?#{#searchDto.ueGrade}% ) AND
            ( ?#{#searchDto.ucGrade} IS null OR c.uc_grade like %?#{#searchDto.ucGrade}% ) AND
            ( ?#{#searchDto.atGrade} IS null OR c.at_grade like %?#{#searchDto.atGrade}% ) AND
            ( ?#{#searchDto.certValid} IS null OR c.is_valid = ?#{#searchDto.certValid} )
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

    @Query("select c from CertInfo c left join CertEproof d on c.id = d.certInfoId where d.uuid = :uuid and d.version = :version")
    CertInfo findEmail(@Param("uuid") String uuid, @Param("version") Integer version);
}
