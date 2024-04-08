package com.hkgov.ceo.pms.mapper;

import com.hkgov.ceo.pms.dto.MeetingWorkspaceHasPostMeetingDocumentDto;
import com.hkgov.ceo.pms.entity.MeetingWorkspaceHasPostMeetingDocument;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface MeetingWorkspaceHasPostMeetingDocumentMapper {
    MeetingWorkspaceHasPostMeetingDocumentMapper INSTANCE = Mappers.getMapper(MeetingWorkspaceHasPostMeetingDocumentMapper.class);

    MeetingWorkspaceHasPostMeetingDocument toEntity(MeetingWorkspaceHasPostMeetingDocumentDto meetingWorkspaceHasPostMeetingDocumentDto);

    MeetingWorkspaceHasPostMeetingDocumentDto toDto(MeetingWorkspaceHasPostMeetingDocument meetingWorkspaceHasPostMeetingDocument);

    List<MeetingWorkspaceHasPostMeetingDocument> toEntityList(List<MeetingWorkspaceHasPostMeetingDocumentDto> meetingWorkspaceHasPostMeetingDocumentDto);

    List<MeetingWorkspaceHasPostMeetingDocumentDto> toDtoList(List<MeetingWorkspaceHasPostMeetingDocument> meetingWorkspaceHasPostMeetingDocument);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    MeetingWorkspaceHasPostMeetingDocument partialUpdate(MeetingWorkspaceHasPostMeetingDocumentDto meetingWorkspaceHasPostMeetingDocumentDto, @MappingTarget MeetingWorkspaceHasPostMeetingDocument meetingWorkspaceHasPostMeetingDocument);
}