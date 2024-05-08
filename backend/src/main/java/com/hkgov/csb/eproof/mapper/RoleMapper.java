package com.hkgov.csb.eproof.mapper;


import com.hkgov.csb.eproof.dto.RoleDto;
import com.hkgov.csb.eproof.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
* @author 20768
* @description 针对表【role】的数据库操作Mapper
* @createDate 2024-04-23 14:06:28
* @Entity com.hkgov.ceo.pms.domain.Role
*/
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface RoleMapper {
    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

    RoleDto sourceToDestination(Role source);

    Role destinationToSource(RoleDto dto);

}




