package com.hkgov.csb.eproof.mapper;

import com.hkgov.csb.eproof.dto.FileDto;
import com.hkgov.csb.eproof.entity.File;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FileMapper {
    FileMapper INSTANCE = Mappers.getMapper(FileMapper.class);

    FileDto sourceToDestination(File source);

    File destinationToSource(FileDto dto);

    List<FileDto> sourceToDestinationList(List<File> files);

    List<File> destinationToSourceList(List<FileDto> files);
}
