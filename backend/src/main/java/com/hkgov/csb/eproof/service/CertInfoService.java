package com.hkgov.csb.eproof.service;

import com.hkgov.csb.eproof.dto.CertImportDto;
import com.hkgov.csb.eproof.dto.CertSearchDto;
import com.hkgov.csb.eproof.entity.CertInfo;
import com.hkgov.csb.eproof.entity.enums.CertStage;
import com.hkgov.csb.eproof.entity.enums.CertStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

/**
* @author David
* @description 针对表【cert_info】的数据库操作Service
* @createDate 2024-05-10 17:47:40
*/
public interface CertInfoService {
    Page<CertInfo> search(CertSearchDto request, List<String> certStageList, List<String> certStatusList, Pageable pageable);

    Boolean batchImport(String examProfileSerialNo, LocalDate examDate, List<CertImportDto> csvData);

    Boolean dispatch(String examProfileSerialNo, CertStage currentStage);


    void changeStatusToInProgress(String examProfileSerialNo, CertStage certStage);

    void batchGeneratePdf(String examProfileSerialNo);
}
