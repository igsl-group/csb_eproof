package com.hkgov.ceo.pms.mapper;

import com.hkgov.ceo.pms.dto.FileDto;
import com.hkgov.ceo.pms.entity.File;
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
