package com.hkgov.csb.eproof.service;

import com.hkgov.csb.eproof.dto.CertRenewSearchDto;
import com.hkgov.csb.eproof.dto.CertRevokeDto;
import com.hkgov.csb.eproof.entity.CertInfoRenew;
import com.hkgov.csb.eproof.entity.enums.CertStage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;

/**
* @author 20768
* @description 针对表【cert_info_renew】的数据库操作Service
* @createDate 2024-06-05 17:19:02
*/
public interface CertInfoRenewService {
    void changeCertStatusToInProgress(Long certInfoId, CertStage certStage);

    void batchGeneratePdf(Long certInfoId) throws Exception;

    void removeCert(Long certInfoId);

    byte [] getZippedPdfBinary(List<Long> certInfoIdList) throws IOException;

    void revoke(List<Long> certInfoIdList, CertRevokeDto params);

    CertRevokeDto getTodoRevoke();

    Page<CertInfoRenew> search(CertRenewSearchDto request, List<String> certStageList, List<String> certStatusList, Pageable pageable);
}
