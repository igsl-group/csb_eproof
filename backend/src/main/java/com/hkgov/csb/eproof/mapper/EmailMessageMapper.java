package com.hkgov.csb.eproof.mapper;

import com.hkgov.csb.eproof.dto.EmailLogDto;
import com.hkgov.csb.eproof.entity.EmailMessage;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
* @author David
* @description 针对表【email_message】的数据库操作Mapper
* @createDate 2024-09-26 15:59:42
* @Entity com.hkgov.ceo.pms.entity.EmailMessage
*/
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface EmailMessageMapper {
    EmailMessageMapper INSTANCE = Mappers.getMapper(EmailMessageMapper.class);

    EmailLogDto sourceToDestination(EmailMessage source);
}
