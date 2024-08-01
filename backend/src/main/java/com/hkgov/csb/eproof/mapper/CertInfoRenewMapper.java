package com.hkgov.csb.eproof.mapper;

import com.hkgov.csb.eproof.dto.CertInfoRenewDto;
import com.hkgov.csb.eproof.entity.CertInfoRenew;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
* @author 20768
* @description 针对表【cert_info_renew】的数据库操作Mapper
* @createDate 2024-06-05 17:19:02
* @Entity com.hkgov.ceo.pms.entity.CertInfoRenew
*/
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CertInfoRenewMapper{
    CertInfoRenewMapper INSTANCE = Mappers.getMapper(CertInfoRenewMapper.class);

    List<CertInfoRenewDto> toDtoList(List<CertInfoRenew> certInfoList);
}
