package com.hkgov.ceo.pms.mapper;

import com.hkgov.ceo.pms.dto.TaskDto;
import com.hkgov.ceo.pms.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TaskMapper {
    TaskMapper INSTANCE = Mappers.getMapper(TaskMapper.class);

    @Mapping(target = "assignedTo", source = "user")
    TaskDto sourceToDestination(Task source);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "meetingWorkspace", ignore = true)
    Task destinationToSource(TaskDto dto);

    List<TaskDto> sourceToDestinationList(List<Task> tasks);

    List<Task> destinationToSourceList(List<TaskDto> tasks);

    void updateFromDto(TaskDto dto, @MappingTarget Task task);
}
