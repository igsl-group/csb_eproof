package com.hkgov.csb.eproof.service;

import com.hkgov.csb.eproof.entity.CertInfo;
import com.hkgov.csb.eproof.entity.enums.CertStage;
import com.hkgov.csb.eproof.entity.enums.CertStatus;

public interface PdfGenerateService {

    void singleGeneratePdf(CertInfo certInfo,
                           byte[] atLeastOnePassedTemplate,
                           byte [] allFailedTemplate,
                           boolean isBatchMode, boolean isNewCertInfo) throws Exception;

    void updateCertStageAndStatus(CertInfo certInfo, CertStage stage, CertStatus status);
}
