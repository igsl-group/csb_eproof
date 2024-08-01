package com.hkgov.csb.eproof.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
* @author 20768
* @description 针对表【action_target】的数据库操作Mapper
* @createDate 2024-07-30 16:44:14
* @Entity com.hkgov.ceo.pms.entity.ActionTarget
*/
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ActionTargetMapper {


}
