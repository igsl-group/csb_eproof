package com.hkgov.csb.eproof.mapper;

import com.hkgov.csb.eproof.dto.MeetingWorkspaceHasPrivateDocumentDto;
import com.hkgov.csb.eproof.entity.MeetingWorkspaceHasPrivateDocument;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface MeetingWorkspaceHasPrivateDocumentMapper {
    MeetingWorkspaceHasPrivateDocumentMapper INSTANCE = Mappers.getMapper(MeetingWorkspaceHasPrivateDocumentMapper.class);

    MeetingWorkspaceHasPrivateDocument toEntity(MeetingWorkspaceHasPrivateDocumentDto meetingWorkspaceHasPrivateDocumentDto);

    MeetingWorkspaceHasPrivateDocumentDto toDto(MeetingWorkspaceHasPrivateDocument meetingWorkspaceHasPrivateDocument);

    List<MeetingWorkspaceHasPrivateDocument> toEntityList(List<MeetingWorkspaceHasPrivateDocumentDto> meetingWorkspaceHasPrivateDocumentDto);

    List<MeetingWorkspaceHasPrivateDocumentDto> toDtoList(List<MeetingWorkspaceHasPrivateDocument> meetingWorkspaceHasPrivateDocument);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    MeetingWorkspaceHasPrivateDocument partialUpdate(MeetingWorkspaceHasPrivateDocumentDto meetingWorkspaceHasPrivateDocumentDto, @MappingTarget MeetingWorkspaceHasPrivateDocument meetingWorkspaceHasPrivateDocument);
}