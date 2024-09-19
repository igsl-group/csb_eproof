package com.hkgov.csb.eproof.dao;

import com.hkgov.csb.eproof.entity.CombinedHisResultBefApprove;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CombinedHisResultBefApproveRepository extends JpaRepository<CombinedHisResultBefApprove,Long> {
    @Query("SELECT c FROM CombinedHisResultBefApprove c WHERE c.status = 'REJECTED' OR c.status = 'PENDING' ")
    List<CombinedHisResultBefApprove> findByStatus();
}
