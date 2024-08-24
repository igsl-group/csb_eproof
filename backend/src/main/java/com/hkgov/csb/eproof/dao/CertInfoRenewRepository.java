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

import java.util.List;

@Repository
public interface CertInfoRenewRepository extends JpaRepository<CertInfoRenew,Long> {
    @Query(nativeQuery = true,value = """
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
            ( ?#{#searchDto.oldAtGrade} IS null OR c.old_at_grade like %?#{#searchDto.oldAtGrade}%  ) 
    
        )
""",countQuery = """
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
            ( ?#{#searchDto.oldAtGrade} IS null OR c.old_at_grade like %?#{#searchDto.oldAtGrade}%  ) 
        ) 
""")
    Page<CertInfoRenew> certSearch(@Param("searchDto") CertRenewSearchDto searchDto,
                              @Param("certStageList") @NotNull List<String> certStageList,
                              @Param("certStatusList") @NotNull List<String> certStatusList,
                              Pageable pageable);

    @Query("select c from CertInfoRenew c where c.id = :id and c.certStage = :stage and c.certStatus = 'SUCCESS' ")
    List<CertInfoRenew> getinfoByNoAndStatus(@Param("id") Long id, @Param("stage") CertStage stage);
}


