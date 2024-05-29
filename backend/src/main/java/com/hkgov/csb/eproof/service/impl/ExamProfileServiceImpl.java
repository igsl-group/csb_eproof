package com.hkgov.csb.eproof.service.impl;

import com.hkgov.csb.eproof.dao.CertInfoRepository;
import com.hkgov.csb.eproof.dao.ExamProfileRepository;
import com.hkgov.csb.eproof.dto.ExamProfileDto;
import com.hkgov.csb.eproof.entity.ExamProfile;
import com.hkgov.csb.eproof.exception.GenericException;
import com.hkgov.csb.eproof.mapper.ExamProfileMapper;
import com.hkgov.csb.eproof.service.ExamProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static com.hkgov.csb.eproof.exception.ExceptionConstants.SERIAL_HAS_EXITED;

@RequiredArgsConstructor
@Service
public class ExamProfileServiceImpl implements ExamProfileService {

    private final ExamProfileRepository examProfileRepository;

    private final CertInfoRepository certInfoRepository;

    @Override
    public Boolean create(ExamProfileDto request) {
        var examProfile = examProfileRepository.getinfoByNo(request.getSerialNo());
        if(Objects.nonNull(examProfile))
            throw new GenericException("400",SERIAL_HAS_EXITED);
        ExamProfile exam = ExamProfileMapper.INSTANCE.destinationToSource(request);
        exam.setIsFreezed(false);
        exam = examProfileRepository.save(exam);
        return Objects.nonNull(exam);
    }

    @Override
    public Boolean freeze(String examProfileSerialNo) {
        return examProfileRepository.updateIsFreezed(examProfileSerialNo) > 0;
    }

    @Override
    public ExamProfile getexamProfileInfo(String examProfileSerialNo) {
        return examProfileRepository.getinfoByNo(examProfileSerialNo);
    }

    @Override
    public Page<ExamProfile> list(Pageable pageable, String keyWord) {
        var examProfile = examProfileRepository.findPage(pageable,keyWord);
        return examProfile;
    }

    @Override
    public List<ExamProfile> dropDown() {
        return examProfileRepository.dropDown();
    }

    @Override
    public Boolean delete(String examProfileSerialNo) {
        var certInfo = certInfoRepository.getInfoByNo(examProfileSerialNo);
        if(Objects.nonNull(certInfo)){
            throw new GenericException("400",SERIAL_HAS_EXITED);
        }
        return examProfileRepository.delExamProfile(examProfileSerialNo) > 0;
    }
}
