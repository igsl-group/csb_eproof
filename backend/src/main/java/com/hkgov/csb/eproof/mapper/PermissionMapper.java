package com.hkgov.csb.eproof.mapper;

import com.hkgov.csb.eproof.dto.PermissionDto;
import com.hkgov.csb.eproof.entity.Permission;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PermissionMapper {
    PermissionMapper INSTANCE = Mappers.getMapper(PermissionMapper.class);

    PermissionDto sourceToDestination(Permission source);

    Permission destinationToSource(PermissionDto dto);

    List<PermissionDto> sourceToDestinationList(List<Permission> permissions);

    List<Permission> destinationToSourceList(List<PermissionDto> permissions);
}
