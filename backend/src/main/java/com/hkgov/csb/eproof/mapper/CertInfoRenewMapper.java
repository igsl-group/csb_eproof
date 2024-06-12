package com.hkgov.csb.eproof.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
* @author 20768
* @description 针对表【cert_info_renew】的数据库操作Mapper
* @createDate 2024-06-05 17:19:02
* @Entity com.hkgov.ceo.pms.entity.CertInfoRenew
*/
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CertInfoRenewMapper{


}
