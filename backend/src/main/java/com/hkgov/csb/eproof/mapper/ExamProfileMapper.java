package com.hkgov.csb.eproof.mapper;

import com.hkgov.csb.eproof.dto.ExamProfileDto;
import com.hkgov.csb.eproof.entity.ExamProfile;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ExamProfileMapper {
    ExamProfileMapper INSTANCE = Mappers.getMapper(ExamProfileMapper.class);

    ExamProfileDto sourceToDestination(ExamProfile source);

    ExamProfile destinationToSource(ExamProfileDto dto);

    void updateFromDto(ExamProfileDto dto, @MappingTarget ExamProfile user);

    List<ExamProfileDto> sourceToDestinationList (List<ExamProfile> source);
}
