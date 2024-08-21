package com.hkgov.csb.eproof.dao;

import com.hkgov.csb.eproof.entity.CombinedHistoricalResultBefore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CombinedHistoricalResultBeforeRepository extends JpaRepository<CombinedHistoricalResultBefore,Long> {
    @Query("select c from CombinedHistoricalResultBefore c where c.hkid in :hkids or c.passport in :passports order by c.hkid,c.passport")
    List<CombinedHistoricalResultBefore> findByHkidIn(List<String> hkids, List<String> passports);
}