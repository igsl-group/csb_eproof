package com.hkgov.csb.eproof.dao;

import com.hkgov.csb.eproof.dto.CertRenewSearchDto;
import com.hkgov.csb.eproof.entity.CertInfoRenew;
import com.hkgov.csb.eproof.entity.enums.CertStage;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface CertInfoRenewRepository
        extends JpaRepository<CertInfoRenew, Long> {
    @Query(nativeQuery = true,
            value = """
                            SELECT * FROM cert_info_renew c WHERE
                            (   (c.is_delete = false OR c.is_delete is null) AND
                                (c.cert_stage IN :certStageList) AND
                                (c.status IN :certStatusList)
                            )
                            AND
                            (
                               ( ?#{#searchDto.newName} IS null OR c.new_name like %?#{#searchDto.newName}% ) AND
                                ( ?#{#searchDto.newHkid} IS null OR c.new_hkid like %?#{#searchDto.newHkid}% ) AND
                                ( ?#{#searchDto.newPassport} IS null OR c.new_passport like %?#{#searchDto.newPassport}% ) AND
                                ( ?#{#searchDto.newEmail} IS null OR c.new_email like %?#{#searchDto.newEmail}% ) AND
                                ( ?#{#searchDto.oldName} IS null OR c.old_name like %?#{#searchDto.oldName}% ) AND
                                ( ?#{#searchDto.oldHkid} IS null OR c.old_hkid like %?#{#searchDto.oldHkid}% ) AND
                                ( ?#{#searchDto.oldPassport} IS null OR c.old_passport like %?#{#searchDto.oldPassport}% ) AND
                                ( ?#{#searchDto.oldEmail} IS null OR c.old_email like %?#{#searchDto.oldEmail}% ) AND
                                ( ?#{#searchDto.newBlGrade} IS null OR c.new_bl_grade like %?#{#searchDto.newBlGrade}%  ) AND
                                ( ?#{#searchDto.newUeGrade} IS null OR c.new_ue_grade like %?#{#searchDto.newUeGrade}%  ) AND
                                ( ?#{#searchDto.newUcGrade} IS null OR c.new_uc_grade like %?#{#searchDto.newUcGrade}%  ) AND
                                ( ?#{#searchDto.newAtGrade} IS null OR c.new_at_grade like %?#{#searchDto.newAtGrade}%  ) AND
                                ( ?#{#searchDto.oldBlGrade} IS null OR c.old_bl_grade like %?#{#searchDto.oldBlGrade}%  ) AND
                                ( ?#{#searchDto.oldUeGrade} IS null OR c.old_ue_grade like %?#{#searchDto.oldUeGrade}%  ) AND
                                ( ?#{#searchDto.oldUcGrade} IS null OR c.old_uc_grade like %?#{#searchDto.oldUcGrade}%  ) AND
                                ( ?#{#searchDto.oldAtGrade} IS null OR c.old_at_grade like %?#{#searchDto.oldAtGrade}%  ) AND
                                ( ?#{#searchDto.oldLetterType} IS null OR c.old_letter_type like %?#{#searchDto.oldLetterType}%  ) AND
                                ( ?#{#searchDto.newLetterType} IS null OR c.new_letter_type like %?#{#searchDto.newLetterType}%  )

                            )
                    """,
            countQuery = """
                     SELECT count(*) FROM cert_info_renew c WHERE
                            (   c.is_delete = false AND
                                (c.cert_stage IN :certStageList) AND
                                (c.status IN :certStatusList)
                            )
                            AND
                            (
                               ( ?#{#searchDto.newName} IS null OR c.new_name like %?#{#searchDto.newName}% ) AND
                                ( ?#{#searchDto.newHkid} IS null OR c.new_hkid like %?#{#searchDto.newHkid}% ) AND
                                ( ?#{#searchDto.newPassport} IS null OR c.new_passport like %?#{#searchDto.newPassport}% ) AND
                                ( ?#{#searchDto.newEmail} IS null OR c.new_email like %?#{#searchDto.newEmail}% ) AND
                                ( ?#{#searchDto.oldName} IS null OR c.old_name like %?#{#searchDto.oldName}% ) AND
                                ( ?#{#searchDto.oldHkid} IS null OR c.old_hkid like %?#{#searchDto.oldHkid}% ) AND
                                ( ?#{#searchDto.oldPassport} IS null OR c.old_passport like %?#{#searchDto.oldPassport}% ) AND
                                ( ?#{#searchDto.oldEmail} IS null OR c.old_email like %?#{#searchDto.oldEmail}% ) AND
                                ( ?#{#searchDto.newBlGrade} IS null OR c.new_bl_grade like %?#{#searchDto.newBlGrade}%  ) AND
                                ( ?#{#searchDto.newUeGrade} IS null OR c.new_ue_grade like %?#{#searchDto.newUeGrade}%  ) AND
                                ( ?#{#searchDto.newUcGrade} IS null OR c.new_uc_grade like %?#{#searchDto.newUcGrade}%  ) AND
                                ( ?#{#searchDto.newAtGrade} IS null OR c.new_at_grade like %?#{#searchDto.newAtGrade}%  ) AND
                                ( ?#{#searchDto.oldBlGrade} IS null OR c.old_bl_grade like %?#{#searchDto.oldBlGrade}%  ) AND
                                ( ?#{#searchDto.oldUeGrade} IS null OR c.old_ue_grade like %?#{#searchDto.oldUeGrade}%  ) AND
                                ( ?#{#searchDto.oldUcGrade} IS null OR c.old_uc_grade like %?#{#searchDto.oldUcGrade}%  ) AND
                                ( ?#{#searchDto.oldAtGrade} IS null OR c.old_at_grade like %?#{#searchDto.oldAtGrade}%  ) AND
                                ( ?#{#searchDto.oldLetterType} IS null OR c.old_letter_type like %?#{#searchDto.oldLetterType}%  ) AND
                                ( ?#{#searchDto.newLetterType} IS null OR c.new_letter_type like %?#{#searchDto.newLetterType}%  )
                            )
                    """)
    Page<CertInfoRenew> certSearch(
            @Param("searchDto") CertRenewSearchDto searchDto,
            @Param("certStageList") @NotNull List<String> certStageList,
            @Param("certStatusList") @NotNull List<String> certStatusList,
            Pageable pageable);

    @Query("select c from CertInfoRenew c where c.id = :id and c.certStage = :stage and c.certStatus = 'SUCCESS' ")
    List<CertInfoRenew> getinfoByNoAndStatus(@Param("id") Long id,
            @Param("stage") CertStage stage);

    @Query("select c from CertInfoRenew c where c.certStatus <> 'COMPLETED' and c.oldHkid = :hkid")
    List<CertInfoRenew> getInfoByHkid(String hkid);

    @Query("select c from CertInfoRenew c where c.certStatus <> 'COMPLETED' and c.oldPassport = :passport")
    List<CertInfoRenew> getInfoByPassport(String passport);

    @Query(value = "SELECT " + "c.new_name AS candidateName, "
            + "COALESCE(c.new_hkid, c.old_hkid) AS hkidNumber, "
            + "COALESCE(c.new_passport, c.old_passport) AS passportNumber, "
            + "CASE " + "    WHEN c.old_name <> c.new_name THEN 'Name' "
            + "    WHEN c.old_hkid <> c.new_hkid THEN 'HKID' "
            + "    WHEN c.old_passport <> c.new_passport THEN 'Passport' "
            + "    WHEN c.old_email <> c.new_email THEN 'Email' "
            + "    ELSE 'None' " + "END AS personalParticularsUpdated, "
            + "CASE "
            + "    WHEN c.old_name <> c.new_name THEN CONCAT('Name: ', COALESCE(c.old_name, '')) "
            + "    WHEN c.old_hkid <> c.new_hkid THEN CONCAT('HKID: ', COALESCE(c.old_hkid, '')) "
            + "    WHEN c.old_passport <> c.new_passport THEN CONCAT('Passport: ', COALESCE(c.old_passport, '')) "
            + "    WHEN c.old_email <> c.new_email THEN CONCAT('Email: ', COALESCE(c.old_email, '')) "
            + "    ELSE NULL " + "END AS oldValue, " + "CASE "
            + "    WHEN c.old_name <> c.new_name THEN CONCAT('Name: ', COALESCE(c.new_name, '')) "
            + "    WHEN c.old_hkid <> c.new_hkid THEN CONCAT('HKID: ', COALESCE(c.new_hkid, '')) "
            + "    WHEN c.old_passport <> c.new_passport THEN CONCAT('Passport: ', COALESCE(c.new_passport, '')) "
            + "    WHEN c.old_email <> c.new_email THEN CONCAT('Email: ', COALESCE(c.new_email, '')) "
            + "    ELSE NULL " + "END AS newValue, " + "c.remark AS remarks, "
            + "c.modified_date AS modifiedDate " + "FROM cert_info_renew c "
            + "WHERE c.modified_date BETWEEN :startDate AND :endDate "
            + "AND (:candidateName IS NULL OR TRIM(:candidateName) = '' OR c.new_name LIKE CONCAT('%', :candidateName, '%'))"
            + "AND (:hkidNumber IS NULL OR TRIM(:hkidNumber) = '' OR c.new_hkid LIKE CONCAT('%', :hkidNumber, '%'))"
            + "AND (:passportNumber IS NULL OR TRIM(:passportNumber) = '' OR c.new_passport LIKE CONCAT('%', :passportNumber, '%'))"
            + "  AND (CASE "
            + "        WHEN c.old_name <> c.new_name THEN 'Name' "
            + "        WHEN c.old_hkid <> c.new_hkid THEN 'HKID' "
            + "        WHEN c.old_passport <> c.new_passport THEN 'Passport' "
            + "        WHEN c.old_email <> c.new_email THEN 'Email' "
            + "        ELSE 'None' " + "      END <> 'None')",
            nativeQuery = true)
    List<Object[]> findPersonalParticularsData(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("candidateName") String candidateName,
            @Param("hkidNumber") String hkidNumber,
            @Param("passportNumber") String passportNumber);

    @Query(value = "SELECT " + "c.new_name AS candidateName, "
            + "COALESCE(c.new_hkid, c.old_hkid) AS hkidNumber, "
            + "COALESCE(c.new_passport, c.old_passport) AS passportNumber, "
            + "CASE "
            + "    WHEN c.old_uc_grade <> c.new_uc_grade THEN 'UC Grade' "
            + "    WHEN c.old_ue_grade <> c.new_ue_grade THEN 'UE Grade' "
            + "    WHEN c.old_bl_grade <> c.new_bl_grade THEN 'BL Grade' "
            + "    WHEN c.old_at_grade <> c.new_at_grade THEN 'AT Grade' "
            + "    ELSE 'None' " + "END AS resultUpdated, " + "CASE "
            + "    WHEN c.old_uc_grade <> c.new_uc_grade THEN CONCAT('UC Grade: ', COALESCE(c.old_uc_grade, '')) "
            + "    WHEN c.old_ue_grade <> c.new_ue_grade THEN CONCAT('UE Grade: ', COALESCE(c.old_ue_grade, '')) "
            + "    WHEN c.old_bl_grade <> c.new_bl_grade THEN CONCAT('BL Grade: ', COALESCE(c.old_bl_grade, '')) "
            + "    WHEN c.old_at_grade <> c.new_at_grade THEN CONCAT('AT Grade: ', COALESCE(c.old_at_grade, '')) "
            + "    ELSE NULL " + "END AS oldValue, " + "CASE "
            + "    WHEN c.old_uc_grade <> c.new_uc_grade THEN CONCAT('UC Grade: ', COALESCE(c.new_uc_grade, '')) "
            + "    WHEN c.old_ue_grade <> c.new_ue_grade THEN CONCAT('UE Grade: ', COALESCE(c.new_ue_grade, '')) "
            + "    WHEN c.old_bl_grade <> c.new_bl_grade THEN CONCAT('BL Grade: ', COALESCE(c.new_bl_grade, '')) "
            + "    WHEN c.old_at_grade <> c.new_at_grade THEN CONCAT('AT Grade: ', COALESCE(c.new_at_grade, '')) "
            + "    ELSE NULL " + "END AS newValue, " + "c.remark AS remarks, "
            + "c.modified_date AS modifiedDate " + "FROM cert_info_renew c "
            + "WHERE c.modified_date BETWEEN :startDate AND :endDate "
            + "AND (:candidateName IS NULL OR TRIM(:candidateName) = '' OR c.new_name LIKE CONCAT('%', :candidateName, '%'))"
            + "AND (:hkidNumber IS NULL OR TRIM(:hkidNumber) = '' OR c.new_hkid LIKE CONCAT('%', :hkidNumber, '%'))"
            + "AND (:passportNumber IS NULL OR TRIM(:passportNumber) = '' OR c.new_passport LIKE CONCAT('%', :passportNumber, '%'))"
            + "AND (CASE "
            + "    WHEN c.old_uc_grade <> c.new_uc_grade THEN 'UC Grade' "
            + "    WHEN c.old_ue_grade <> c.new_ue_grade THEN 'UE Grade' "
            + "    WHEN c.old_bl_grade <> c.new_bl_grade THEN 'BL Grade' "
            + "    WHEN c.old_at_grade <> c.new_at_grade THEN 'AT Grade' "
            + "        ELSE 'None' " + "END <> 'None')", nativeQuery = true)
    List<Object[]> findResultData(@Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("candidateName") String candidateName,
            @Param("hkidNumber") String hkidNumber,
            @Param("passportNumber") String passportNumber);
}


