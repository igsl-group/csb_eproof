package com.hkgov.csb.eproof.mapper;

import com.hkgov.csb.eproof.dto.EmailTemplateDto;
import com.hkgov.csb.eproof.entity.EmailTemplate;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
* @author 20768
* @description 针对表【email_template】的数据库操作Mapper
* @createDate 2024-06-17 10:17:32
* @Entity com.hkgov.ceo.pms.entity.EmailTemplate
*/
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface EmailTemplateMapper {
    EmailTemplateMapper INSTANCE = Mappers.getMapper(EmailTemplateMapper.class);

    EmailTemplateDto sourceToDestination(EmailTemplate source);

    EmailTemplate destinationToSource(EmailTemplateDto dto);

}
