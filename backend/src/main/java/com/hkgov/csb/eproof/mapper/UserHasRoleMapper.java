package com.hkgov.csb.eproof.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
* @author David
* @description 针对表【user_has_role】的数据库操作Mapper
* @createDate 2024-04-23 14:06:40
* @Entity com.hkgov.ceo.pms.domain.UserHasRole
*/
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserHasRoleMapper{
}




