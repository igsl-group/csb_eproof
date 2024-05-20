package com.hkgov.csb.eproof.service.impl;

import com.hkgov.csb.eproof.dao.CertInfoRepository;
import com.hkgov.csb.eproof.dto.CertSearchDto;
import com.hkgov.csb.eproof.entity.CertInfo;
import com.hkgov.csb.eproof.service.CertInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author David
* @description 针对表【cert_info】的数据库操作Service实现
* @createDate 2024-05-10 17:47:40
*/
@Service
@RequiredArgsConstructor
public class CertInfoServiceImpl implements CertInfoService {
    private final CertInfoRepository certInfoRepository;


    @Override
    public Page<CertInfo> search(CertSearchDto request, List<String> certStageList, List<String> certStatusList, Pageable pageable) {
        return certInfoRepository.caseSearch(request,certStageList,certStatusList,pageable);
    }
}
