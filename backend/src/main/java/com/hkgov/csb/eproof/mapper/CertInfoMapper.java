package com.hkgov.csb.eproof.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
* @author David
* @description 针对表【cret_info】的数据库操作Mapper
* @createDate 2024-05-10 17:47:40
* @Entity com.hkgov.ceo.pms.entity.CertInfo
*/
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CertInfoMapper {
    CertInfoMapper INSTANCE = Mappers.getMapper(CertInfoMapper.class);

}
