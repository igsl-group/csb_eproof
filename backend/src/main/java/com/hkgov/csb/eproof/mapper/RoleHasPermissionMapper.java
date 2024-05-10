package com.hkgov.csb.eproof.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
* @author David
* @description 针对表【role_has_permission】的数据库操作Mapper
* @createDate 2024-05-06 10:20:02
* @Entity com.hkgov.ceo.pms.entity.RoleHasPermission
*/
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface RoleHasPermissionMapper {


}
