package com.hkgov.csb.eproof.dao;

import com.hkgov.csb.eproof.dto.CertSearchDto;
import com.hkgov.csb.eproof.entity.CertInfo;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CertInfoRepository extends JpaRepository<CertInfo, Long> {
    @Query("select c from CertInfo c where c.examProfileSerialNo = :serialNo")
    CertInfo getInfoByNo(@Param("serialNo") String serialNo);

    List<CertInfo> getInfoByExamProfileSerialNo(
            @Param("serialNo") String serialNo);

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
            CertStage stage, List<CertStatus> statusList);

    @Query(nativeQuery = true,
            value = """
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
                                ( ?#{#searchDto.letterType} IS null OR c.letter_type like %?#{#searchDto.letterType}% ) AND
                                ( ?#{#searchDto.examDateFrom} IS null OR c.exam_date >= ?#{#searchDto.examDateFrom}) AND
                                ( ?#{#searchDto.examDateTo} IS null OR c.exam_date <= ?#{#searchDto.examDateTo})
                            )
                    """,
            countQuery = """
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
                                ( ?#{#searchDto.letterType} IS null OR c.letter_type like %?#{#searchDto.letterType}% ) AND
                                ( ?#{#searchDto.examDateFrom} IS null OR c.exam_date >= ?#{#searchDto.examDateFrom}) AND
                                ( ?#{#searchDto.examDateTo} IS null OR c.exam_date <= ?#{#searchDto.examDateTo})
                            )
                    """)
    Page<CertInfo> certSearch(@Param("searchDto") CertSearchDto searchDto,
            @Param("certStageList") @NotNull List<String> certStageList,
            @Param("certStatusList") @NotNull List<String> certStatusList,
            Pageable pageable);

    @Query("SELECT c FROM CertInfo c WHERE c.id in :certInfoIdList")
    List<CertInfo> getByIdIn(List<Long> certInfoIdList);

    @Query("select c from CertInfo c where c.examProfileSerialNo = :serialNo and ((c.certStage = 'NOTIFY' and c.certStatus = 'SUCCESS' or c.certStatus = 'FAIL') or (c.certStage = 'COMPLETED'))")
    List<CertInfo> getInfoWithNotifyAndCompletedStageList(
            @Param("serialNo") String serialNo);

    @Query("select c from CertInfo c where c.examProfileSerialNo = :serialNo")
    List<CertInfo> getInfoListByExamSerialNo(
            @Param("serialNo") String serialNo);

    @Query("select c from CertInfo c where c.examProfileSerialNo = :serialNo and c.certStage= :stage and c.certStatus = 'SUCCESS' and c.onHold = false  ")
    List<CertInfo> getinfoByNoAndStatus(@Param("serialNo") String serialNo,
            @Param("stage") CertStage stage);


    @Query("""
                SELECT COUNT(c) FROM CertInfo c
                WHERE (c.examProfileSerialNo = :examProfileSerialNo and c.certStage = :certStage and c.onHold = false)
            """)
    Integer countByStageWithOnHold(String examProfileSerialNo, CertStage certStage);

    @Query("""
                SELECT COUNT(c) FROM CertInfo c
                WHERE (c.certStatus = :certStatus AND c.examProfileSerialNo = :examProfileSerialNo and c.certStage = :certStage and c.onHold = false)
            """)
    Integer countByStageAndStatus(String examProfileSerialNo,
            CertStage certStage, CertStatus certStatus);

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
    CertInfo findEmail(@Param("uuid") String uuid,
            @Param("version") Integer version);

    @Query(nativeQuery = true,
            value = "select * from cert_info c where c.exam_profile_serial = :examProfileSerialNo and c.cert_stage = 'SIGN_ISSUE' and c.status = 'SCHEDULED' and c.on_hold = false order by c.id LIMIT 1")
    CertInfo getNextScheduledSignAndIssueCert(String examProfileSerialNo);

    @Query("select c from CertInfo c where (c.passed is null or c.passed = false ) and c.valid = true and (c.hkid in :hkids or c.passportNo in :passports) order by c.examDate,c.hkid,c.passportNo")
    List<CertInfo> findByHkidIn(List<String> hkids, List<String> passports);

    @Modifying
    @Query("""
                UPDATE CertInfo
                SET certStage = :nextStage , certStatus= :pendingCertStatus, valid = :isValid,  modifiedBy = :currentUserName, modifiedDate = current_timestamp
                WHERE examProfileSerialNo = :examSerialNo and certStage = :currentStage and certStatus = 'SUCCESS' and onHold = false  
            """)
    Integer dispatchCert(String examSerialNo, CertStage currentStage,
            CertStage nextStage, CertStatus pendingCertStatus, Boolean isValid,
            String currentUserName);


    @Modifying
    @Query("""
                UPDATE CertInfo
                SET certStatus = :scheduledStatus, modifiedBy = :dpUserId, modifiedDate = current_timestamp
                WHERE certStatus in :inProgressAndPending and examProfileSerialNo = :examProfileSerialNo
                and certStage = :signAndIssueStage AND onHold = false
            """)
    void batchScheduledSignAndIssue(String examProfileSerialNo,
            CertStage signAndIssueStage, List<CertStatus> inProgressAndPending,
            CertStatus scheduledStatus, String dpUserId);

    @Query(nativeQuery = true, value = """
            SELECT c.* FROM cert_info c
            LEFT JOIN exam_profile ep ON c.exam_profile_serial = ep.serial_no
            WHERE c.exam_profile_serial = :examProfileSerialNo
            AND (c.blnst_grade IN (:blnstGrade) OR c.blnst_grade = '')
            AND (c.ue_grade IN (:ueGrade) OR c.ue_grade = '')
            AND (c.uc_grade IN (:ucGrade) OR c.uc_grade = '')
            AND (c.at_grade IN (:atGrade) OR c.at_grade = '')
            ORDER BY RAND()
            LIMIT :limit
            """)
    List<CertInfo> getRandomCert(
            @Param("examProfileSerialNo") String examProfileSerialNo,
            @Param("blnstGrade") List<String> blnstGrade,
            @Param("ueGrade") List<String> ueGrade,
            @Param("ucGrade") List<String> ucGrade,
            @Param("atGrade") List<String> atGrade,
            @Param("limit") Integer limit);



    @Query(nativeQuery = true,
            value = """
                             SELECT c.* FROM cert_info c
                             LEFT JOIN exam_profile ep on c.exam_profile_serial = ep.serial_no
                                WHERE c.exam_profile_serial = :examProfileSerialNo
                                 /* At least 1 failed */ AND (
                                     (c.blnst_grade in :blnstFailGrade or c.blnst_grade = '')
                                     OR (c.ue_grade in :ueFailGrade or c.ue_grade = '')
                                     OR (c.uc_grade in :ucFailGrade or c.uc_grade = '')
                                     OR (c.at_grade in :atFailGrade or c.at_grade = '')
                                 )
                                /* Not all failed */ AND NOT(
                                     (c.blnst_grade in :blnstFailGrade or c.blnst_grade = '')
                                     AND (c.ue_grade in :ueFailGrade or c.ue_grade = '')
                                     AND (c.uc_grade in :ucFailGrade or c.uc_grade = '')
                                     AND (c.at_grade in :atFailGrade or c.at_grade = '')
                                )
                                /* Contains some passed grade */ AND(
                                     (c.blnst_grade in :blnstPassGrade or c.blnst_grade = '')
                                     OR (c.ue_grade in :uePassGrade or c.ue_grade = '')
                                     OR (c.uc_grade in :ucPassGrade or c.uc_grade = '')
                                     OR (c.at_grade in :atPassGrade or c.at_grade = '')
                                )
                                ORDER BY rand()
                            LIMIT :limit
                    """)
    List<CertInfo> getPartialFailedCert(String examProfileSerialNo,
            List<String> blnstPassGrade, List<String> blnstFailGrade,
            List<String> uePassGrade, List<String> ueFailGrade,
            List<String> ucPassGrade, List<String> ucFailGrade,
            List<String> atPassGrade, List<String> atFailGrade, Integer limit);

    @Query(value = "SELECT " + "exam_profile_serial, "
            + "COUNT(CASE WHEN uc_grade IS NOT NULL AND uc_grade <> '' THEN 1 END) AS uc_total_candidate, "
            + "ROUND(COUNT(CASE WHEN uc_grade = 'L2' THEN 1 END) / NULLIF(COUNT(CASE WHEN uc_grade IS NOT NULL AND uc_grade <> '' THEN 1 END), 0),2) AS uc_no_of_L2, "
            + "ROUND(COUNT(CASE WHEN uc_grade = 'L1' THEN 1 END) / NULLIF(COUNT(CASE WHEN uc_grade IS NOT NULL AND uc_grade <> '' THEN 1 END), 0),2) AS uc_no_of_L1, "
            + "COUNT(CASE WHEN ue_grade IS NOT NULL AND ue_grade <> '' THEN 1 END) AS ue_total_candidate, "
            + "ROUND(COUNT(CASE WHEN ue_grade = 'L2' THEN 1 END) / NULLIF(COUNT(CASE WHEN ue_grade IS NOT NULL AND ue_grade <> '' THEN 1 END), 0),2) AS ue_no_of_L2, "
            + "ROUND(COUNT(CASE WHEN ue_grade = 'L1' THEN 1 END) / NULLIF(COUNT(CASE WHEN ue_grade IS NOT NULL AND ue_grade <> '' THEN 1 END), 0),2) AS ue_no_of_L1, "
            + "COUNT(CASE WHEN at_grade IS NOT NULL AND at_grade <> '' THEN 1 END) AS at_total_candidate, "
            + "ROUND(COUNT(CASE WHEN at_grade = 'P' THEN 1 END) / NULLIF(COUNT(CASE WHEN at_grade IS NOT NULL AND at_grade <> '' THEN 1 END), 0),2) AS at_pass_rate, "
            + "ROUND(COUNT(CASE WHEN at_grade = 'F' THEN 1 END) / NULLIF(COUNT(CASE WHEN at_grade IS NOT NULL AND at_grade <> '' THEN 1 END), 0),2) AS at_fail_rate, "
            + "COUNT(CASE WHEN blnst_grade IS NOT NULL AND blnst_grade <> '' THEN 1 END) AS blnst_total_candidate, "
            + "ROUND(COUNT(CASE WHEN blnst_grade = 'P' THEN 1 END) / NULLIF(COUNT(CASE WHEN blnst_grade IS NOT NULL AND blnst_grade <> '' THEN 1 END), 0),2) AS blnst_pass_rate, "
            + "ROUND(COUNT(CASE WHEN blnst_grade = 'F' THEN 1 END) / NULLIF(COUNT(CASE WHEN blnst_grade IS NOT NULL AND blnst_grade <> '' THEN 1 END), 0),2) AS blnst_fail_rate "
            + "FROM cert_info "
            + "WHERE exam_profile_serial LIKE %:examProfileSerialNo% "
            + "AND (:startDate IS NULL AND :endDate IS NULL OR exam_date BETWEEN :startDate AND :endDate) "
            + "GROUP BY exam_profile_serial", nativeQuery = true)
    List<Object[]> findReportData(@Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate, @Param("examProfileSerialNo") String examProfileSerialNo);

    @Query(value = "SELECT " + "YEAR(exam_date) AS year, "
            + "COUNT(CASE WHEN uc_grade IS NOT NULL AND uc_grade <> '' THEN 1 END) AS uc_total_candidate, "
            + "ROUND(COUNT(CASE WHEN uc_grade = 'L2' THEN 1 END) / NULLIF(COUNT(CASE WHEN uc_grade IS NOT NULL AND uc_grade <> '' THEN 1 END), 0),2) AS uc_no_of_L2, "
            + "ROUND(COUNT(CASE WHEN uc_grade = 'L1' THEN 1 END) / NULLIF(COUNT(CASE WHEN uc_grade IS NOT NULL AND uc_grade <> '' THEN 1 END), 0),2) AS uc_no_of_L1, "
            + "COUNT(CASE WHEN ue_grade IS NOT NULL AND ue_grade <> '' THEN 1 END) AS ue_total_candidate, "
            + "ROUND(COUNT(CASE WHEN ue_grade = 'L2' THEN 1 END) / NULLIF(COUNT(CASE WHEN ue_grade IS NOT NULL AND ue_grade <> '' THEN 1 END), 0),2) AS ue_no_of_L2, "
            + "ROUND(COUNT(CASE WHEN ue_grade = 'L1' THEN 1 END) / NULLIF(COUNT(CASE WHEN ue_grade IS NOT NULL AND ue_grade <> '' THEN 1 END), 0),2) AS ue_no_of_L1, "
            + "COUNT(CASE WHEN at_grade IS NOT NULL AND at_grade <> '' THEN 1 END) AS at_total_candidate, "
            + "ROUND(COUNT(CASE WHEN at_grade = 'P' THEN 1 END) / NULLIF(COUNT(CASE WHEN at_grade IS NOT NULL AND at_grade <> '' THEN 1 END), 0),2) AS at_pass_rate, "
            + "ROUND(COUNT(CASE WHEN at_grade = 'F' THEN 1 END) / NULLIF(COUNT(CASE WHEN at_grade IS NOT NULL AND at_grade <> '' THEN 1 END), 0),2) AS at_fail_rate, "
            + "COUNT(CASE WHEN blnst_grade IS NOT NULL AND blnst_grade <> '' THEN 1 END) AS blnst_total_candidate, "
            + "ROUND(COUNT(CASE WHEN blnst_grade = 'P' THEN 1 END) / NULLIF(COUNT(CASE WHEN blnst_grade IS NOT NULL AND blnst_grade <> '' THEN 1 END), 0),2) AS blnst_pass_rate, "
            + "ROUND(COUNT(CASE WHEN blnst_grade = 'F' THEN 1 END) / NULLIF(COUNT(CASE WHEN blnst_grade IS NOT NULL AND blnst_grade <> '' THEN 1 END), 0),2) AS blnst_fail_rate "
            + "FROM cert_info " + "WHERE YEAR(exam_date) = :year "
            + "GROUP BY YEAR(exam_date)", nativeQuery = true)
    List<Object[]> findReportData(@Param("year") int year);

    @Modifying
    @Query("""
    UPDATE CertInfo ci 
    SET ci.gcisBatchEmailId = null 
    where ci.gcisBatchEmail.scheduleDatetime >= :tomorrow
    and ci.certStage = 'NOTIFY'
    and ci.examProfileSerialNo = :examProfileSerialNo
""")
    void updateNotYetSentCertBatchEmailToNull(String examProfileSerialNo, LocalDateTime tomorrow);

    @Transactional
    @Modifying
    @Query("UPDATE CertInfo ci set ci.certStatus = 'SUCCESS', ci.actualEmailSendTime = :actualEmailSendTime where ci.certStage = 'NOTIFY' and ci.gcisBatchEmailId = :gcisBatchEmailId")
    void updateNotifyStatusByGcisBatchEmailId(@Param("gcisBatchEmailId") Long gcisBatchEmailId, @Param("actualEmailSendTime") LocalDateTime actualEmailSendTime);

    @Query("""
    SELECT ci From CertInfo ci
    where ci.certStage='NOTIFY'
    and ci.gcisBatchEmailId is null
    and ci.examProfileSerialNo = :examProfileSerialNo
""")
    List<CertInfo> getToBeSendBatchEmailCert(String examProfileSerialNo);

    @Query("select c.id from CertInfo c where c.examProfileSerialNo = :examProfileId")
    List<Long> getAllByExamProfileId(String examProfileId);
}
