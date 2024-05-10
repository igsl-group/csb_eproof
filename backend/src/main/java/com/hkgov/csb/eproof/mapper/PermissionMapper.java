package com.hkgov.csb.eproof.mapper;

import com.hkgov.csb.eproof.dto.PermissionDto;
import com.hkgov.csb.eproof.entity.Permission;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
* @author David
* @description 针对表【permission】的数据库操作Mapper
* @createDate 2024-04-26 17:15:34
* @Entity com.hkgov.ceo.pms.entity.Permission
*/
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface PermissionMapper {
    PermissionMapper INSTANCE = Mappers.getMapper(PermissionMapper.class);

    PermissionDto sourceToDestination(Permission source);

    Permission destinationToSource(PermissionDto dto);
}
