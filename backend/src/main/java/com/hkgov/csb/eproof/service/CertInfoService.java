package com.hkgov.csb.eproof.service;

import com.hkgov.csb.eproof.dto.CertImportDto;
import com.hkgov.csb.eproof.dto.CertSearchDto;
import com.hkgov.csb.eproof.entity.CertInfo;
import com.hkgov.csb.eproof.entity.enums.CertStage;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.io.InputStream;
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


    void changeCertStatusToInProgress(String examProfileSerialNo, CertStage certStage);

    void batchGeneratePdf(String examProfileSerialNo) throws Exception;

    void generatePdf(CertInfo certInfo,
                     byte[] atLeastOnePassedTemplate,
                     byte [] allFailedTemplate,
                     boolean isBatchMode) throws Exception;
}
