package com.hkgov.ceo.pms.mapper;

import com.hkgov.ceo.pms.dto.UserSessionDto;
import com.hkgov.ceo.pms.entity.UserSession;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static com.hkgov.ceo.pms.config.Constants.HH_MM_SS_STRING_FORMAT;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserSessionMapper {
    UserSessionMapper INSTANCE = Mappers.getMapper(UserSessionMapper.class);

    @Mapping(source = "userName", target = "user.name")
    @Mapping(source = "loginId", target = "user.loginId")
    UserSession toEntity(UserSessionDto userSessionDto);

    @Mapping(target = "token", ignore = true)
    @Mapping(source = "user.name", target = "userName")
    @Mapping(source = "user.loginId", target = "loginId")
    @Mapping(source = "timeStamp", target = "connectionTime", qualifiedByName = "calculateConnectionTime")
    UserSessionDto toDto(UserSession userSession);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "userName", target = "user.name")
    UserSession partialUpdate(UserSessionDto userSessionDto, @MappingTarget UserSession userSession);

    List<UserSessionDto> toDtoList(List<UserSession> userSession);

    @Named("calculateConnectionTime")
    static String calculateConnectionTime(LocalDateTime timeStamp) {
        Duration duration = Duration.between(timeStamp, LocalDateTime.now());
        return String.format(HH_MM_SS_STRING_FORMAT, duration.toHoursPart(), duration.toMinutesPart(), duration.toSecondsPart());
    }
}