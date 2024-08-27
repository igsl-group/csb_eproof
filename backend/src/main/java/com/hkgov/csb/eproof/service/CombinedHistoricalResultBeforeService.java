package com.hkgov.csb.eproof.service;

import com.hkgov.csb.eproof.dto.HistoricalSearchDto;
import com.hkgov.csb.eproof.dto.UpdateHistoricalDto;
import com.hkgov.csb.eproof.entity.CombinedHistoricalResultBefore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
* @author David
* @description 针对表【combined_historical_result_before_2024】的数据库操作Service
* @createDate 2024-08-16 15:53:54
*/
public interface CombinedHistoricalResultBeforeService {

    Page<CombinedHistoricalResultBefore> list(Pageable pageable, HistoricalSearchDto searchDto);

    void valid(Long id, String remark);

    void invalid(Long id, String remark);

    void updateGrade(Long id, UpdateHistoricalDto dto);
}
