package com.hkgov.ceo.pms.mapper;

import com.hkgov.ceo.pms.dto.RoleDto;
import com.hkgov.ceo.pms.entity.Role;
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
