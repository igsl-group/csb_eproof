package com.hkgov.csb.eproof.mapper;

import com.hkgov.csb.eproof.dto.ExamProfileDto;
import com.hkgov.csb.eproof.dto.FileDto;
import com.hkgov.csb.eproof.entity.ExamProfile;
import com.hkgov.csb.eproof.entity.File;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;


@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface FileMapper {

    List<FileDto> sourceToDestinationList(List<File> source);

}
