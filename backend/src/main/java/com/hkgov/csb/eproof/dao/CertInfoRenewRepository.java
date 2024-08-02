package com.hkgov.csb.eproof.dao;

import com.hkgov.csb.eproof.dto.CertRenewSearchDto;
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
           ( ?#{#searchDto.name} IS null OR c.new_cname like %?#{#searchDto.name}% ) AND
            ( ?#{#searchDto.hkid} IS null OR c.new_hkid like %?#{#searchDto.hkid}% ) AND
            ( ?#{#searchDto.cname} IS null OR c.new_cname like %?#{#searchDto.cname}% ) AND
            ( ?#{#searchDto.passportNo} IS null OR c.new_passport like %?#{#searchDto.passportNo}% ) AND
            ( ?#{#searchDto.email} IS null OR c.new_email = ?#{#searchDto.email} )
        )
""",countQuery = """
 SELECT count(*) FROM cert_info_renew c WHERE
        (
            (c.cert_stage IN :certStageList) AND
            (c.status IN :certStatusList)
        )
        AND
        (
            ( ?#{#searchDto.name} IS null OR c.new_cname like %?#{#searchDto.name}% ) AND
            ( ?#{#searchDto.hkid} IS null OR c.new_hkid like %?#{#searchDto.hkid}% ) AND
            ( ?#{#searchDto.cname} IS null OR c.new_cname like %?#{#searchDto.cname}% ) AND
            ( ?#{#searchDto.passportNo} IS null OR c.new_passport like %?#{#searchDto.passportNo}% ) AND
            ( ?#{#searchDto.email} IS null OR c.new_email = ?#{#searchDto.email} )
        )
""")
    Page<CertInfoRenew> certSearch(@Param("searchDto") CertRenewSearchDto searchDto,
                              @Param("certStageList") @NotNull List<String> certStageList,
                              @Param("certStatusList") @NotNull List<String> certStatusList,
                              Pageable pageable);
}
