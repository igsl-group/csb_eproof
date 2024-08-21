package com.hkgov.csb.eproof.dao;

import com.hkgov.csb.eproof.entity.CombinedHistoricalResultBefore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CombinedHistoricalResultBeforeRepository extends JpaRepository<CombinedHistoricalResultBefore,Long> {
    @Query("select c from CombinedHistoricalResultBefore c where c.hkid in :hkids or c.passport in :passports order by c.hkid,c.passport")
    List<CombinedHistoricalResultBefore> findByHkidIn(List<String> hkids, List<String> passports);

    @Query("select c from CombinedHistoricalResultBefore c where :keyword is null or (c.name like %:keyword% or c.hkid like %:keyword% " +
            "or c.passport like %:keyword% or c.blGrade like %:keyword% or c.atGrade like %:keyword% or c.ueGrade like %:keyword% or c.ueGrade like %:keyword%)")
    Page<CombinedHistoricalResultBefore> findPage(Pageable pageable, @Param("keyword") String keyword);
}