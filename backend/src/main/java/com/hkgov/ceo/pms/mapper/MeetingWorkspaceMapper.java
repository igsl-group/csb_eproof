package com.hkgov.ceo.pms.mapper;

import com.hkgov.ceo.pms.dto.MeetingWorkspaceDto;
import com.hkgov.ceo.pms.entity.MeetingWorkspace;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MeetingWorkspaceMapper {
    MeetingWorkspaceMapper INSTANCE = Mappers.getMapper(MeetingWorkspaceMapper.class);

    MeetingWorkspaceDto sourceToDestination(MeetingWorkspace source);

    MeetingWorkspace destinationToSource(MeetingWorkspaceDto dto);

    List<MeetingWorkspaceDto> sourceToDestinationList(List<MeetingWorkspace> meetingWorkspaces);

    List<MeetingWorkspace> destinationToSourceList(List<MeetingWorkspaceDto> meetingWorkspaces);

    void updateFromDto(MeetingWorkspaceDto dto, @MappingTarget MeetingWorkspace meetingWorkspace);
}
