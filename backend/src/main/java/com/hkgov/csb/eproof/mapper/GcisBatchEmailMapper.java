package com.hkgov.csb.eproof.mapper;

import com.hkgov.csb.eproof.dto.GcisBatchEmailDto;
import com.hkgov.csb.eproof.entity.GcisBatchEmail;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface GcisBatchEmailMapper {
    GcisBatchEmail toEntity(GcisBatchEmailDto gcisBatchEmailDto);

    GcisBatchEmailDto toDto(GcisBatchEmail gcisBatchEmail);

}