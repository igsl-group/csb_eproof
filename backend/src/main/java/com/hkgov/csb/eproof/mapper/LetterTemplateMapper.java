package com.hkgov.csb.eproof.mapper;

import com.hkgov.csb.eproof.dto.LetterTemplateDto;
import com.hkgov.csb.eproof.entity.LetterTemplate;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface LetterTemplateMapper {

    LetterTemplateMapper INSTANCE = Mappers.getMapper(LetterTemplateMapper.class);

    LetterTemplate toEntity(LetterTemplateDto letterTemplateDto);

    LetterTemplateDto toDto(LetterTemplate letterTemplate);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    LetterTemplate partialUpdate(LetterTemplateDto letterTemplateDto, @MappingTarget LetterTemplate letterTemplate);
}