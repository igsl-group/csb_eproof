package com.hkgov.ceo.pms.mapper;

import com.hkgov.ceo.pms.dto.PermissionDto;
import com.hkgov.ceo.pms.entity.Permission;
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
