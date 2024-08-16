package com.hkgov.csb.eproof.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
* @author David
* @description 针对表【combined_historical_result_before_2024】的数据库操作Mapper
* @createDate 2024-08-16 15:53:54
* @Entity com.hkgov.ceo.pms.entity.CombinedHistoricalResultBefore
*/
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CombinedHistoricalResultBeforeMapper {

}
