package com.hkgov.csb.eproof.dao;

import com.hkgov.csb.eproof.dto.CertSearchDto;
import com.hkgov.csb.eproof.dto.ExamResultReportDTO;
import com.hkgov.csb.eproof.entity.CertInfo;
import com.hkgov.csb.eproof.entity.User;
import com.hkgov.csb.eproof.entity.enums.CertStage;
import com.hkgov.csb.eproof.entity.enums.CertStatus;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
@Repository
public interface CertInfoRepository extends JpaRepository<CertInfo,Long> {
    @Query("select c from CertInfo c where c.examProfileSerialNo = :serialNo")
    CertInfo getInfoByNo(@Param("serialNo") String serialNo);
    List<CertInfo> getInfoByExamProfileSerialNo(@Param("serialNo") String serialNo);

    @Query(value = """
select c from CertInfo c
        left join fetch c.examProfile
        left join fetch c.pdfList
        left join fetch c.certInfoRenewList
        left join fetch c.certEproof
        where c.examProfileSerialNo = :serialNo 
        AND c.certStage = :stage 
        AND c.certStatus in :statusList
        AND c.onHold = false
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
            ( ?#{#searchDto.canEmail} IS null OR c.email like %?#{#searchDto.canEmail}% ) AND
            ( ?#{#searchDto.examProfileSerialNo} IS null OR c.exam_profile_serial = ?#{#searchDto.examProfileSerialNo} ) AND
            ( ?#{#searchDto.blnstGrade} IS null OR c.blnst_grade like %?#{#searchDto.blnstGrade}% ) AND
            ( ?#{#searchDto.ueGrade} IS null OR c.ue_grade like %?#{#searchDto.ueGrade}% ) AND
            ( ?#{#searchDto.ucGrade} IS null OR c.uc_grade like %?#{#searchDto.ucGrade}% ) AND
            ( ?#{#searchDto.atGrade} IS null OR c.at_grade like %?#{#searchDto.atGrade}% ) AND
            ( ?#{#searchDto.certValid} IS null OR c.is_valid = ?#{#searchDto.certValid} ) AND
            ( ?#{#searchDto.onHold} IS null OR c.on_hold = ?#{#searchDto.onHold} ) AND 
            ( ?#{#searchDto.letterType} IS null OR c.letter_type like %?#{#searchDto.letterType}% )
        )
""",countQuery = """
 SELECT count(*) FROM cert_info c WHERE
        (
            (c.cert_stage IN :certStageList) AND
            (c.status IN :certStatusList)
        )
        AND
        (
            ( ?#{#searchDto.hkid} IS null OR c.hkid like %?#{#searchDto.hkid}% ) AND
            ( ?#{#searchDto.passportNo} IS null OR c.passport_no like %?#{#searchDto.passportNo}% ) AND
            ( ?#{#searchDto.canName} IS null OR c.name like %?#{#searchDto.canName}% ) AND
            ( ?#{#searchDto.canEmail} IS null OR c.email like %?#{#searchDto.canEmail}% ) AND
            ( ?#{#searchDto.examProfileSerialNo} IS null OR c.exam_profile_serial = ?#{#searchDto.examProfileSerialNo} ) AND
            ( ?#{#searchDto.blnstGrade} IS null OR c.blnst_grade like %?#{#searchDto.blnstGrade}% ) AND
            ( ?#{#searchDto.ueGrade} IS null OR c.ue_grade like %?#{#searchDto.ueGrade}% ) AND
            ( ?#{#searchDto.ucGrade} IS null OR c.uc_grade like %?#{#searchDto.ucGrade}% ) AND
            ( ?#{#searchDto.atGrade} IS null OR c.at_grade like %?#{#searchDto.atGrade}% ) AND
            ( ?#{#searchDto.certValid} IS null OR c.is_valid = ?#{#searchDto.certValid} )AND
            ( ?#{#searchDto.onHold} IS null OR c.on_hold = ?#{#searchDto.onHold} ) AND 
            ( ?#{#searchDto.letterType} IS null OR c.letter_type like %?#{#searchDto.letterType}% )
        )
""")
    Page<CertInfo> certSearch(@Param("searchDto") CertSearchDto searchDto,
                              @Param("certStageList") @NotNull List<String> certStageList,
                              @Param("certStatusList") @NotNull List<String> certStatusList,
                              Pageable pageable);

    @Query("SELECT c FROM CertInfo c WHERE c.id in :certInfoIdList")
    List<CertInfo> getByIdIn(List<Long> certInfoIdList);

    @Query("select c from CertInfo c where c.examProfileSerialNo = :serialNo and ((c.certStage = 'NOTIFY' and c.certStatus = 'SUCCESS' or c.certStatus = 'FAIL') or (c.certStage = 'COMPLETED'))")
    List<CertInfo> getInfoWithNotifyAndCompletedStageList(@Param("serialNo") String serialNo);

    @Query("select c from CertInfo c left join ExamProfile where c.examProfileSerialNo = :serialNo")
    List<CertInfo> getInfoListByExamSerialNo(@Param("serialNo") String serialNo);

    @Query("select c from CertInfo c left join ExamProfile where c.examProfileSerialNo = :serialNo and c.certStage= :stage and c.certStatus = 'SUCCESS' and c.onHold = false  ")
    List<CertInfo> getinfoByNoAndStatus(@Param("serialNo") String serialNo,@Param("stage") CertStage stage);


    @Query("""
    SELECT COUNT(c) FROM CertInfo c
    WHERE (:certStatus != null AND c.certStatus = :certStatus AND c.examProfileSerialNo = :examProfileSerialNo and c.certStage = :certStage and c.onHold = false)
    OR (:certStatus = null AND c.examProfileSerialNo = :examProfileSerialNo and c.certStage = :certStage and c.onHold = false)    
""")
    Integer countByStageAndStatus(String examProfileSerialNo, CertStage certStage, CertStatus certStatus);

    @Query("""
    SELECT COUNT(c) FROM CertInfo c
    WHERE (c.examProfileSerialNo = :examProfileSerialNo and c.onHold = true) 
""")
    Integer countByOnHold(String examProfileSerialNo);

    @Query("select c from CertInfo c where c.hkid = :hkid and c.valid = true")
    List<CertInfo> findAllByHkid(@Param("hkid") String hkid);


    @Query("select c from CertInfo c where c.passportNo = :passportNo and c.valid = true")
    List<CertInfo> findAllByPassport(@Param("passportNo") String passportNo);

    @Query("select c from CertInfo c left join CertEproof d on c.id = d.certInfoId where d.uuid = :uuid and d.version = :version")
    CertInfo findEmail(@Param("uuid") String uuid, @Param("version") Integer version);

    @Query(nativeQuery = true,value="select * from cert_info c where c.exam_profile_serial = :examProfileSerialNo and c.cert_stage = 'SIGN_ISSUE' and c.status = 'SCHEDULED' order by c.id LIMIT 1")
    CertInfo getNextScheduledSignAndIssueCert(String examProfileSerialNo);

    @Query("select c from CertInfo c where (c.passed is null or c.passed = false ) and c.valid = true and (c.hkid in :hkids or c.passportNo in :passports) order by c.examDate,c.hkid,c.passportNo")
    List<CertInfo> findByHkidIn(List<String> hkids,List<String> passports);

    @Modifying
    @Query("""
    UPDATE CertInfo 
    SET certStage = :nextStage , certStatus= :pendingCertStatus, modifiedBy = :currentUserName, modifiedDate = current_timestamp
    WHERE examProfileSerialNo = :examSerialNo and certStage = :currentStage
""")
    Integer dispatchCert(String examSerialNo, CertStage currentStage, CertStage nextStage, CertStatus pendingCertStatus, String currentUserName);


    @Modifying
    @Query("""
    UPDATE CertInfo
    SET certStatus = :scheduledStatus, modifiedBy = :dpUserId, modifiedDate = current_timestamp
    WHERE certStatus in :inProgressAndPending and examProfileSerialNo = :examProfileSerialNo
    and certStage = :signAndIssueStage
""")
    void batchScheduledSignAndIssue(String examProfileSerialNo, CertStage signAndIssueStage,List<CertStatus> inProgressAndPending, CertStatus scheduledStatus, String dpUserId);

    @Query(value = "SELECT " +
    "exam_profile_serial, " +
    "COUNT(CASE WHEN uc_grade IS NOT NULL AND uc_grade <> '' THEN 1 END) AS uc_total_candidate, " +
    "COUNT(CASE WHEN uc_grade = 'L2' THEN 1 END) * 100.0 / NULLIF(COUNT(CASE WHEN uc_grade IS NOT NULL AND uc_grade <> '' THEN 1 END), 0) AS uc_no_of_L2, " +
    "COUNT(CASE WHEN uc_grade = 'L1' THEN 1 END) * 100.0 / NULLIF(COUNT(CASE WHEN uc_grade IS NOT NULL AND uc_grade <> '' THEN 1 END), 0) AS uc_no_of_L1, " +
    "COUNT(CASE WHEN ue_grade IS NOT NULL AND ue_grade <> '' THEN 1 END) AS ue_total_candidate, " +
    "COUNT(CASE WHEN ue_grade = 'L2' THEN 1 END) * 100.0 / NULLIF(COUNT(CASE WHEN ue_grade IS NOT NULL AND ue_grade <> '' THEN 1 END), 0) AS ue_no_of_L2, " +
    "COUNT(CASE WHEN ue_grade = 'L1' THEN 1 END) * 100.0 / NULLIF(COUNT(CASE WHEN ue_grade IS NOT NULL AND ue_grade <> '' THEN 1 END), 0) AS ue_no_of_L1, " +
    "COUNT(CASE WHEN at_grade IS NOT NULL AND at_grade <> '' THEN 1 END) AS at_total_candidate, " +
    "COUNT(CASE WHEN at_grade = 'P' THEN 1 END) * 100.0 / NULLIF(COUNT(CASE WHEN at_grade IS NOT NULL AND at_grade <> '' THEN 1 END), 0) AS at_pass_rate, " +
    "COUNT(CASE WHEN at_grade = 'F' THEN 1 END) * 100.0 / NULLIF(COUNT(CASE WHEN at_grade IS NOT NULL AND at_grade <> '' THEN 1 END), 0) AS at_fail_rate, " +
    "COUNT(CASE WHEN blnst_grade IS NOT NULL AND blnst_grade <> '' THEN 1 END) AS blnst_total_candidate, " +
    "COUNT(CASE WHEN blnst_grade = 'P' THEN 1 END) * 100.0 / NULLIF(COUNT(CASE WHEN blnst_grade IS NOT NULL AND blnst_grade <> '' THEN 1 END), 0) AS blnst_pass_rate, " +
    "COUNT(CASE WHEN blnst_grade = 'F' THEN 1 END) * 100.0 / NULLIF(COUNT(CASE WHEN blnst_grade IS NOT NULL AND blnst_grade <> '' THEN 1 END), 0) AS blnst_fail_rate " +
    "FROM cert_info " +
    "WHERE exam_date BETWEEN :startDate AND :endDate " +
    "GROUP BY exam_profile_serial", nativeQuery = true)
    List<Object[]> findReportData(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query(value = "SELECT " +
    "YEAR(exam_date) AS year, " +
    "COUNT(CASE WHEN uc_grade IS NOT NULL AND uc_grade <> '' THEN 1 END) AS uc_total_candidate, " +
    "COUNT(CASE WHEN uc_grade = 'L2' THEN 1 END) * 100.0 / NULLIF(COUNT(CASE WHEN uc_grade IS NOT NULL AND uc_grade <> '' THEN 1 END), 0) AS uc_no_of_L2, " +
    "COUNT(CASE WHEN uc_grade = 'L1' THEN 1 END) * 100.0 / NULLIF(COUNT(CASE WHEN uc_grade IS NOT NULL AND uc_grade <> '' THEN 1 END), 0) AS uc_no_of_L1, " +
    "COUNT(CASE WHEN ue_grade IS NOT NULL AND ue_grade <> '' THEN 1 END) AS ue_total_candidate, " +
    "COUNT(CASE WHEN ue_grade = 'L2' THEN 1 END) * 100.0 / NULLIF(COUNT(CASE WHEN ue_grade IS NOT NULL AND ue_grade <> '' THEN 1 END), 0) AS ue_no_of_L2, " +
    "COUNT(CASE WHEN ue_grade = 'L1' THEN 1 END) * 100.0 / NULLIF(COUNT(CASE WHEN ue_grade IS NOT NULL AND ue_grade <> '' THEN 1 END), 0) AS ue_no_of_L1, " +
    "COUNT(CASE WHEN at_grade IS NOT NULL AND at_grade <> '' THEN 1 END) AS at_total_candidate, " +
    "COUNT(CASE WHEN at_grade = 'P' THEN 1 END) * 100.0 / NULLIF(COUNT(CASE WHEN at_grade IS NOT NULL AND at_grade <> '' THEN 1 END), 0) AS at_pass_rate, " +
    "COUNT(CASE WHEN at_grade = 'F' THEN 1 END) * 100.0 / NULLIF(COUNT(CASE WHEN at_grade IS NOT NULL AND at_grade <> '' THEN 1 END), 0) AS at_fail_rate, " +
    "COUNT(CASE WHEN blnst_grade IS NOT NULL AND blnst_grade <> '' THEN 1 END) AS blnst_total_candidate, " +
    "COUNT(CASE WHEN blnst_grade = 'P' THEN 1 END) * 100.0 / NULLIF(COUNT(CASE WHEN blnst_grade IS NOT NULL AND blnst_grade <> '' THEN 1 END), 0) AS blnst_pass_rate, " +
    "COUNT(CASE WHEN blnst_grade = 'F' THEN 1 END) * 100.0 / NULLIF(COUNT(CASE WHEN blnst_grade IS NOT NULL AND blnst_grade <> '' THEN 1 END), 0) AS blnst_fail_rate " +
    "FROM cert_info " +
    "WHERE YEAR(exam_date) = :year " +
    "GROUP BY YEAR(exam_date)", nativeQuery = true)
    List<Object[]> findReportData(@Param("year") int year);
}
