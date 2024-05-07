package com.hkgov.csb.eproof.mapper;

import com.hkgov.csb.eproof.dto.RoleDto;
import com.hkgov.csb.eproof.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RoleMapper {
    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

    RoleDto sourceToDestination(Role source);

    Role destinationToSource(RoleDto dto);

    List<RoleDto> sourceToDestinationList(List<Role> roles);

    List<Role> destinationToSourceList(List<RoleDto> roles);
}
