package com.hkgov.csb.eproof.dao;

import com.hkgov.csb.eproof.dto.CertSearchDto;
import com.hkgov.csb.eproof.entity.CertInfoRenew;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CertInfoRenewRepository extends JpaRepository<CertInfoRenew,Long> {
    @Modifying
    @Query("update CertInfoRenew set isDelete = true where id = :id")
    void updateIsDeleteById(@Param("id") Long id);

    @Query(nativeQuery = true,value = """
        SELECT * FROM cert_info_renew c WHERE
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
            ( ?#{#searchDto.certValid} IS null OR c.is_valid = ?#{#searchDto.certValid} ) AND
            ( ?#{#searchDto.onHold} IS null OR c.on_hold = ?#{#searchDto.onHold} )
        )
""",countQuery = """
 SELECT count(*) FROM cert_info_renew c WHERE
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
            ( ?#{#searchDto.certValid} IS null OR c.is_valid = ?#{#searchDto.certValid} )AND
            ( ?#{#searchDto.onHold} IS null OR c.on_hold = ?#{#searchDto.onHold} )
        )
""")
    Page<CertInfoRenew> certSearch(@Param("searchDto") CertSearchDto searchDto,
                              @Param("certStageList") @NotNull List<String> certStageList,
                              @Param("certStatusList") @NotNull List<String> certStatusList,
                              Pageable pageable);
}
