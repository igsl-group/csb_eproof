package com.hkgov.csb.eproof.mapper;

import com.hkgov.csb.eproof.dto.SystemParameterDto;
import com.hkgov.csb.eproof.entity.SystemParameter;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
* @author 20768
* @description 针对表【system_parameter】的数据库操作Mapper
* @createDate 2024-07-09 14:24:09
* @Entity com.hkgov.ceo.pms.entity.SystemParameter
*/
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface SystemParameterMapper{
    SystemParameterMapper INSTANCE = Mappers.getMapper(SystemParameterMapper.class);

    SystemParameterDto sourceToDestination(SystemParameter source);

}
