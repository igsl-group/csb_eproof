package com.hkgov.csb.eproof.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
* @author 20768
* @description 针对表【cert_renew_pdf】的数据库操作Mapper
* @createDate 2024-07-30 16:45:06
* @Entity com.hkgov.ceo.pms.entity.CertRenewPdf
*/
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CertRenewPdfMapper{

}
