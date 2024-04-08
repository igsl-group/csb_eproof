package com.hkgov.ceo.pms.mapper;

import com.hkgov.ceo.pms.dto.AuditLogCsvDto;
import com.hkgov.ceo.pms.dto.AuditLogDto;
import com.hkgov.ceo.pms.dto.AuditLogSearchDto;
import com.hkgov.ceo.pms.entity.AuditLog;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface AuditLogMapper {
    AuditLogMapper INSTANCE = Mappers.getMapper(AuditLogMapper.class);

    AuditLog toEntity(AuditLogDto auditLogDto);

    AuditLogDto toDto(AuditLog auditLog);

    AuditLogSearchDto toSearchDto(AuditLog auditLog);

    @Mapping(source = "actionDateTime", target = "date", qualifiedByName = "actionDateTimeToLocalDate")
    @Mapping(source = "actionDateTime", target = "time", qualifiedByName = "actionDateTimeToLocalTime")
    AuditLogCsvDto toCsvDto(AuditLog auditLog);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    AuditLog partialUpdate(AuditLogDto auditLogDto, @MappingTarget AuditLog auditLog);

    AuditLog toEntity1(AuditLogDto auditLogDto);

    List<AuditLogDto> toDtoList(List<AuditLog> auditLog);

    @Named("actionDateTimeToLocalDate")
    static LocalDate actionDateTimeToLocalDate(LocalDateTime actionDateTime) {
        return actionDateTime.toLocalDate();
    }

    @Named("actionDateTimeToLocalTime")
    static LocalTime actionDateTimeToLocalTime(LocalDateTime actionDateTime) {
        return actionDateTime.toLocalTime().truncatedTo(ChronoUnit.SECONDS);
    }
}