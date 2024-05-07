package com.hkgov.csb.eproof.mapper;

import com.hkgov.csb.eproof.dto.AgendaItemHasDocumentDto;
import com.hkgov.csb.eproof.entity.AgendaItemHasDocument;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface AgendaItemHasDocumentMapper {

    AgendaItemHasDocumentMapper INSTANCE = Mappers.getMapper(AgendaItemHasDocumentMapper.class);

    AgendaItemHasDocument toEntity(AgendaItemHasDocumentDto agendaItemHasDocumentDto);

    @Mapping(target = "agendaItemId", source = "agendaItem.agendaItemId")
    @Mapping(target = "preparedBy.userHasMeetingGroups", ignore = true)
    @Mapping(target = "approvedBy.userHasMeetingGroups", ignore = true)
    @Mapping(target = "rejectedBy.userHasMeetingGroups", ignore = true)
    AgendaItemHasDocumentDto toDto(AgendaItemHasDocument agendaItemHasDocument);

    List<AgendaItemHasDocumentDto> toDtoList(List<AgendaItemHasDocument> agendaItemHasDocument);

    List<AgendaItemHasDocument> toEntityList(List<AgendaItemHasDocumentDto> agendaItemHasDocumentDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    AgendaItemHasDocument partialUpdate(AgendaItemHasDocumentDto agendaItemHasDocumentDto, @MappingTarget AgendaItemHasDocument agendaItemHasDocument);
}