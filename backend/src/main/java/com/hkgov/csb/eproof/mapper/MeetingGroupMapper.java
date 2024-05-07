package com.hkgov.csb.eproof.mapper;

import com.hkgov.csb.eproof.dto.MeetingGroupDto;
import com.hkgov.csb.eproof.entity.MeetingGroup;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MeetingGroupMapper {
    MeetingGroupMapper INSTANCE = Mappers.getMapper(MeetingGroupMapper.class);

    MeetingGroupDto sourceToDestination(MeetingGroup source);

    MeetingGroup destinationToSource(MeetingGroupDto dto);

    List<MeetingGroupDto> sourceToDestinationList(List<MeetingGroup> groups);

    List<MeetingGroup> destinationToSourceList(List<MeetingGroupDto> groups);
}
