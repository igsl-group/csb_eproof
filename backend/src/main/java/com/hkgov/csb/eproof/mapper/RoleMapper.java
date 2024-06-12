package com.hkgov.csb.eproof.mapper;


import com.hkgov.csb.eproof.dto.RoleDto;
import com.hkgov.csb.eproof.entity.Role;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
* @author David
* @description 针对表【role】的数据库操作Mapper
* @createDate 2024-04-23 14:06:28
* @Entity com.hkgov.csb.EProof.domain.Role
*/
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface RoleMapper {
    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

    RoleDto sourceToDestination(Role source);

    Role destinationToSource(RoleDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void partialUpdate(@MappingTarget Role role, RoleDto dto);

}




