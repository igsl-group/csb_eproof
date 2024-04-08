package com.hkgov.ceo.pms.mapper;

import com.hkgov.ceo.pms.dto.AgendaItemDto;
import com.hkgov.ceo.pms.entity.AgendaItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AgendaItemMapper {
    AgendaItemMapper INSTANCE = Mappers.getMapper(AgendaItemMapper.class);

    @Mapping(target = "preparedBy.userHasMeetingGroups", ignore = true)
    @Mapping(target = "approvedBy.userHasMeetingGroups", ignore = true)
    @Mapping(target = "rejectedBy.userHasMeetingGroups", ignore = true)
    AgendaItemDto sourceToDestination(AgendaItem source);

    @Named("sourceToDestinationIgnoreList")
    @Mapping(target = "preparedBy.userHasMeetingGroups", ignore = true)
    @Mapping(target = "approvedBy.userHasMeetingGroups", ignore = true)
    @Mapping(target = "rejectedBy.userHasMeetingGroups", ignore = true)
    @Mapping(target = "agendaItemHasDocuments", ignore = true)
    AgendaItemDto sourceToDestinationIgnoreList(AgendaItem source);

    @Mapping(target = "meetingWorkspace", ignore = true)
    @Mapping(target = "preparedBy", ignore = true)
    @Mapping(target = "approvedBy", ignore = true)
    @Mapping(target = "rejectedBy", ignore = true)
    AgendaItem destinationToSource(AgendaItemDto dto);

    List<AgendaItemDto> sourceToDestinationList(List<AgendaItem> agendaItems);

    List<AgendaItem> destinationToSourceList(List<AgendaItemDto> agendaItems);

    @Mapping(target = "meetingWorkspace", ignore = true)
    @Mapping(target = "preparedBy", ignore = true)
    @Mapping(target = "approvedBy", ignore = true)
    @Mapping(target = "rejectedBy", ignore = true)
    void updateFromDto(AgendaItemDto dto, @MappingTarget AgendaItem agendaItem);
}
