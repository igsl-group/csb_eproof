package com.hkgov.ceo.pms.mapper;

import com.hkgov.ceo.pms.dto.MeetingGroupDto;
import com.hkgov.ceo.pms.entity.MeetingGroup;
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
