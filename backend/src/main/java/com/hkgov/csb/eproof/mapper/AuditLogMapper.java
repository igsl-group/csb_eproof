package com.hkgov.csb.eproof.mapper;

import com.hkgov.csb.eproof.dto.AuditLogListDto;
import com.hkgov.csb.eproof.entity.AuditLog;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface AuditLogMapper {
    AuditLogMapper INSTANCE = Mappers.getMapper(AuditLogMapper.class);

    AuditLogListDto sourceToDestination(AuditLog source);
}
