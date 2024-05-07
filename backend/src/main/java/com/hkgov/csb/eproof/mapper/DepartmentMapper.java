package com.hkgov.csb.eproof.mapper;

import com.hkgov.csb.eproof.dto.DepartmentDto;
import com.hkgov.csb.eproof.entity.Department;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DepartmentMapper {
    DepartmentMapper INSTANCE = Mappers.getMapper(DepartmentMapper.class);

    DepartmentDto sourceToDestination(Department source);

    Department destinationToSource(DepartmentDto dto);

    List<DepartmentDto> sourceToDestinationList(List<Department> departments);

    List<Department> destinationToSourceList(List<DepartmentDto> departments);
}
