package com.hkgov.ceo.pms.mapper;

import com.hkgov.ceo.pms.dto.ConfigurationDto;
import com.hkgov.ceo.pms.entity.Configuration;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ConfigurationMapper {
    ConfigurationMapper INSTANCE = Mappers.getMapper(ConfigurationMapper.class);

    Configuration toEntity(ConfigurationDto configurationDto);

    ConfigurationDto toDto(Configuration configuration);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Configuration partialUpdate(ConfigurationDto configurationDto, @MappingTarget Configuration configuration);
}