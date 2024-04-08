package com.hkgov.ceo.pms.mapper;

import com.hkgov.ceo.pms.data.MeetingWorkspaceRetentionData;
import com.hkgov.ceo.pms.dto.MeetingWorkspaceRetentionDto;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MeetingWorkspaceRetentionMapper {
    MeetingWorkspaceRetentionMapper INSTANCE = Mappers.getMapper(MeetingWorkspaceRetentionMapper.class);

    MeetingWorkspaceRetentionDto toDto(MeetingWorkspaceRetentionData source);
}
