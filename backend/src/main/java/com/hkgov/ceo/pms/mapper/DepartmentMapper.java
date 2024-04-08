package com.hkgov.ceo.pms.mapper;

import com.hkgov.ceo.pms.dto.DepartmentDto;
import com.hkgov.ceo.pms.entity.Department;
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
