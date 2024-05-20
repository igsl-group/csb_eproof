package com.hkgov.csb.eproof.service;

import com.hkgov.csb.eproof.dto.CertSearchDto;
import com.hkgov.csb.eproof.entity.CertInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
* @author David
* @description 针对表【cert_info】的数据库操作Service
* @createDate 2024-05-10 17:47:40
*/
public interface CertInfoService {
    Page<CertInfo> search(CertSearchDto request, List<String> certStageList, List<String> certStatusList, Pageable pageable);
}
