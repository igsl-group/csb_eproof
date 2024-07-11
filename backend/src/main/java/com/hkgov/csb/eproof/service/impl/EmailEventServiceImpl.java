package com.hkgov.csb.eproof.service.impl;

import com.hkgov.csb.eproof.constants.Constants;
import com.hkgov.csb.eproof.constants.enums.ExceptionEnums;
import com.hkgov.csb.eproof.dao.CertInfoRepository;
import com.hkgov.csb.eproof.dao.EmailEventRepository;
import com.hkgov.csb.eproof.dao.ExamProfileRepository;
import com.hkgov.csb.eproof.dto.ExamProfileCreateDto;
import com.hkgov.csb.eproof.dto.ExamProfileSummaryDto;
import com.hkgov.csb.eproof.dto.ExamProfileUpdateDto;
import com.hkgov.csb.eproof.entity.CertInfo;
import com.hkgov.csb.eproof.entity.EmailEvent;
import com.hkgov.csb.eproof.entity.ExamProfile;
import com.hkgov.csb.eproof.entity.enums.CertStage;
import com.hkgov.csb.eproof.entity.enums.CertStatus;
import com.hkgov.csb.eproof.exception.GenericException;
import com.hkgov.csb.eproof.mapper.ExamProfileMapper;
import com.hkgov.csb.eproof.service.EmailEventService;
import com.hkgov.csb.eproof.service.ExamProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static com.hkgov.csb.eproof.exception.ExceptionConstants.SERIAL_HAS_EXITED;

@Service
public class EmailEventServiceImpl implements EmailEventService {
    private final EmailEventRepository emailEventRepository;

    public EmailEventServiceImpl(EmailEventRepository emailEventRepository) {
        this.emailEventRepository = emailEventRepository;
    }

    @Override
    @Transactional
    public void updateEmailEventStatus(List<EmailEvent> emailEventList, String status) {
        emailEventList.forEach(emailEvent -> {
            emailEvent.setStatus(status);
        });
        emailEventRepository.saveAll(emailEventList);
    }
}
