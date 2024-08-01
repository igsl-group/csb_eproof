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

    @Query("SELECT c FROM CertInfoRenew c ")
    Page<CertInfoRenew> certSearch(@Param("searchDto") CertSearchDto searchDto,
                              @Param("certStageList") @NotNull List<String> certStageList,
                              @Param("certStatusList") @NotNull List<String> certStatusList,
                              Pageable pageable);
}
