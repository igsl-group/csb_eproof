package com.hkgov.csb.eproof.mapper;

import com.hkgov.csb.eproof.dto.BatchEmailLogDto;
import com.hkgov.csb.eproof.dto.GcisBatchEmailDto;
import com.hkgov.csb.eproof.entity.GcisBatchEmail;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface GcisBatchEmailMapper {
    GcisBatchEmailMapper INSTANCE = Mappers.getMapper(GcisBatchEmailMapper.class);

    GcisBatchEmail toEntity(GcisBatchEmailDto gcisBatchEmailDto);

    BatchEmailLogDto toDto(GcisBatchEmail gcisBatchEmail);

}