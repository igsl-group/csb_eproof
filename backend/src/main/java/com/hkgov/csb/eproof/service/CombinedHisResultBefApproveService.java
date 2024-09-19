package com.hkgov.csb.eproof.service;

import com.hkgov.csb.eproof.dto.UpdateHisApproveDto;

import java.util.List;

/**
* @description 针对表【combined_historical_result_before_2024_approve】的数据库操作Service
* @createDate 2024-09-18 10:38:07
*/
public interface CombinedHisResultBefApproveService {

    void update(Long id,UpdateHisApproveDto dto);

    void request(UpdateHisApproveDto dto);

    void approve(Long id,UpdateHisApproveDto dto);

    void reject(Long id,UpdateHisApproveDto dto);

    List<UpdateHisApproveDto> list();

    void remove(Long id);
}
