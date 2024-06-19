package com.hkgov.csb.eproof.mapper;

import com.hkgov.csb.eproof.dto.CertImportDto;
import com.hkgov.csb.eproof.dto.CertInfoDto;
import com.hkgov.csb.eproof.entity.CertInfo;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

import java.util.List;


@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CertInfoMapper {
    CertInfoMapper INSTANCE = Mappers.getMapper(CertInfoMapper.class);

    List<CertImportDto> sourceToDestinationList(List<CertInfo> source);

    CertInfo toEntity(CertInfoDto certInfoDto);

    CertInfoDto toDto(CertInfo certInfo);

    List<CertInfoDto> toDtoList(List<CertInfo> certInfoList);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    CertInfo partialUpdate(CertInfoDto certInfoDto, @MappingTarget CertInfo certInfo);
}
