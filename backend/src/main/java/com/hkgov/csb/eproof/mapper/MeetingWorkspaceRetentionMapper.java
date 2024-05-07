package com.hkgov.csb.eproof.mapper;

import com.hkgov.csb.eproof.data.MeetingWorkspaceRetentionData;
import com.hkgov.csb.eproof.dto.MeetingWorkspaceRetentionDto;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MeetingWorkspaceRetentionMapper {
    MeetingWorkspaceRetentionMapper INSTANCE = Mappers.getMapper(MeetingWorkspaceRetentionMapper.class);

    MeetingWorkspaceRetentionDto toDto(MeetingWorkspaceRetentionData source);
}
