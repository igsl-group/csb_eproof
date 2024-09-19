package com.hkgov.csb.eproof.mapper;

import com.hkgov.csb.eproof.dto.UpdateHisApproveDto;
import com.hkgov.csb.eproof.entity.CombinedHisResultBefApprove;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
* @author David
* @description 针对表【combined_historical_result_before_2024_approve】的数据库操作Mapper
* @createDate 2024-09-18 10:38:07
* @Entity com.hkgov.ceo.pms.entity.CombinedHisResultBefApprove
*/
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CombinedHisResultBefApproveMapper {
    CombinedHisResultBefApproveMapper INSTANCE = Mappers.getMapper(CombinedHisResultBefApproveMapper.class);

    CombinedHisResultBefApprove sourceToDestination(UpdateHisApproveDto source);

    UpdateHisApproveDto destinationToSource(CombinedHisResultBefApprove source);
}
