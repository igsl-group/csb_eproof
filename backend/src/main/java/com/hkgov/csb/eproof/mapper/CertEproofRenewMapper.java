package com.hkgov.csb.eproof.mapper;

import com.hkgov.csb.eproof.dto.CertEproofRenewDto;
import com.hkgov.csb.eproof.entity.CertEproofRenew;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;


@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CertEproofRenewMapper {
    CertEproofRenewMapper INSTANCE = Mappers.getMapper(CertEproofRenewMapper.class);

    List<CertEproofRenewDto> sourceToDestinationList(List<CertEproofRenew> source);
    CertEproofRenewDto sourceToDestination(CertEproofRenew source);
}
