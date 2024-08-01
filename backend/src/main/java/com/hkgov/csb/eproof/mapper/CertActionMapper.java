package com.hkgov.csb.eproof.mapper;

import com.hkgov.csb.eproof.dto.CertRevokeDto;
import com.hkgov.csb.eproof.entity.CertAction;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
* @author 20768
* @description 针对表【cert_action】的数据库操作Mapper
* @createDate 2024-07-30 16:44:24
* @Entity com.hkgov.ceo.pms.entity.CertAction
*/
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CertActionMapper {
    CertActionMapper INSTANCE = Mappers.getMapper(CertActionMapper.class);

    CertRevokeDto sourceToDestination(CertAction source);

}
