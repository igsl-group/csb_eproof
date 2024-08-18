package com.hkgov.csb.eproof.service;

import com.hkgov.csb.eproof.entity.CertInfo;

public interface PdfGenerateService {

    void singleGeneratePdf(CertInfo certInfo,
                           byte[] atLeastOnePassedTemplate,
                           byte [] allFailedTemplate,
                           boolean isBatchMode, boolean isNewCertInfo) throws Exception;
}
