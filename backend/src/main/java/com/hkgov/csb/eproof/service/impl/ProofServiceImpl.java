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
import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProofServiceImpl implements ProofService {
    private final CertInfoRepository certInfoRepository;
    private final EmailTemplateRepository emailTemplateRepository;
    private final EmailUtil emailUtil;
    private final EmailMessageRepository emailMessageRepository;
    private final EmailEventRepository emailEventRepository;
    private final CertEproofRepository eproofRepository;
    private static final String STATUS = "PENDING";
    private static final String RESPONSE_STATUS = "successful";
    @Override
    public EproofResponseDto sendOTP(ProofDto requestDto) {
        CertInfo certInfo = certInfoRepository.findEmail(requestDto.getUuid(),requestDto.getVersion());
        EmailTemplate emailTemplate = emailTemplateRepository.findByName("OTP");
        Map<String,Object> map = new HashMap<>();
        map.put("otp", requestDto.getOtp());
        EmailMessage message = new EmailMessage();
        message.setTo(certInfo.getEmail());
        message.setSubject(emailTemplate.getSubject());
        try {
            String content = emailUtil.getRenderedHtml(emailTemplate.getBody(),map);
            message.setBody(content);
        } catch (IOException e) {
            throw new GenericException(ExceptionEnums.EMAIL_CONTENT_ERROR);
        } catch (TemplateException e) {
            throw new GenericException(ExceptionEnums.EMAIL_CONTENT_ERROR);
        }
        message = emailMessageRepository.save(message);
        EmailEvent emailEvent = new EmailEvent();
        emailEvent.setEmailMessageId(message.getId());
        emailEvent.setStatus(STATUS);
        emailEventRepository.save(emailEvent);
        return new EproofResponseDto(RESPONSE_STATUS,"");
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
        String path = eproofRepository.getPath(requestDto.getUuid(),requestDto.getVersion());
        if(Objects.isNull(path)){
            throw new GenericException(ExceptionEnums.E_PROOF_NOT_FOUND);
        }
        return new EproofResponseDto(RESPONSE_STATUS,"", Base64.getEncoder().encodeToString(path.getBytes()));
    }
}
