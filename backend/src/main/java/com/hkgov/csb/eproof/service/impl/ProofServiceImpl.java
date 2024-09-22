package com.hkgov.csb.eproof.service.impl;

import com.hkgov.csb.eproof.constants.enums.ExceptionEnums;
import com.hkgov.csb.eproof.dao.*;
import com.hkgov.csb.eproof.dto.EproofDataDto;
import com.hkgov.csb.eproof.dto.EproofResponseDto;
import com.hkgov.csb.eproof.dto.ProofDto;
import com.hkgov.csb.eproof.entity.*;
import com.hkgov.csb.eproof.exception.GenericException;
import com.hkgov.csb.eproof.service.ProofService;
import com.hkgov.csb.eproof.util.EmailUtil;
import com.hkgov.csb.eproof.util.MinioUtil;
import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.nio.file.Files;
import java.util.Base64;
@Service
@RequiredArgsConstructor
public class ProofServiceImpl implements ProofService {
    private final CertInfoRepository certInfoRepository;
    private final EmailTemplateRepository emailTemplateRepository;
    private final EmailUtil emailUtil;
    private final MinioUtil minioUtil;
    private final EmailMessageRepository emailMessageRepository;
    private final EmailEventRepository emailEventRepository;
    private final CertEproofRepository eproofRepository;
    private final GcisEmailServiceImpl gcisEmailServiceImpl;
    private final FileRepository fileRepository;
    private static final String STATUS = "PENDING";
    private static final String RESPONSE_STATUS = "Successful";
    @Override
    public EproofResponseDto sendOTP(ProofDto requestDto) {
        CertInfo certInfo = certInfoRepository.findEmail(requestDto.getUuid(),requestDto.getVersion());
        EmailTemplate emailTemplate = emailTemplateRepository.findByName("OTP");
        Map<String,Object> map = new HashMap<>();
        map.put("otp", requestDto.getOtp());
        String htmlBody = "";
        try {
            htmlBody = emailUtil.getRenderedHtml(emailTemplate.getBody(),map);

        } catch (IOException e) {
            throw new GenericException(ExceptionEnums.EMAIL_CONTENT_ERROR);
        } catch (TemplateException e) {
            throw new GenericException(ExceptionEnums.EMAIL_CONTENT_ERROR);
        }
        gcisEmailServiceImpl.sendTestEmail(certInfo.getEmail(), emailTemplate.getSubject(), htmlBody);

        return new EproofResponseDto(RESPONSE_STATUS,"Received OTP and send the OTP to candidate via GCIS Email notification service");
    }

    @Override
    public EproofResponseDto getData(EproofDataDto requestDto) {
        CertEproof certEproof = eproofRepository.findByUuidAndVersion(requestDto.getUuid(),requestDto.getVersion());
        if(Objects.isNull(certEproof)){
            throw new GenericException(ExceptionEnums.E_PROOF_NOT_FOUND);
        }
        return new EproofResponseDto(RESPONSE_STATUS,"",certEproof.getEWalletJson());
    }

    @Override
    public EproofResponseDto getPdf(EproofDataDto requestDto) {
        CertEproof certEproof = eproofRepository.findByUuidAndVersion(requestDto.getUuid(),requestDto.getVersion());
        if(Objects.isNull(certEproof)){
            throw new GenericException(ExceptionEnums.E_PROOF_NOT_FOUND);
        }

        File latestPdf = fileRepository.getLatestPdfForCert(certEproof.getCertInfoId());
        byte[] certPdfBinary = minioUtil.getFileAsByteArray(latestPdf.getPath());

        if (certPdfBinary == null || certPdfBinary.length == 0) {
            throw new GenericException(ExceptionEnums.E_PROOF_NOT_FOUND);
        }

        return new EproofResponseDto(RESPONSE_STATUS,"", Base64.getEncoder().encodeToString(certPdfBinary));
    }
}
