package com.hkgov.csb.eproof.mapper;

import com.hkgov.csb.eproof.dto.CertEproofDto;
import com.hkgov.csb.eproof.entity.CertEproof;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;


@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CertEproofMapper {
    CertEproofMapper INSTANCE = Mappers.getMapper(CertEproofMapper.class);

    List<CertEproofDto> sourceToDestinationList(List<CertEproof> source);
    CertEproofDto sourceToDestination(CertEproof source);
}
