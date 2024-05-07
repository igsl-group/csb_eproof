package com.hkgov.csb.eproof.mapper;

import com.hkgov.csb.eproof.dto.MeetingWorkspaceDto;
import com.hkgov.csb.eproof.entity.MeetingWorkspace;
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
