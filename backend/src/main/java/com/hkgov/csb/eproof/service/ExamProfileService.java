package com.hkgov.csb.eproof.service;


import com.hkgov.csb.eproof.dto.ExamProfileCreateDto;
import com.hkgov.csb.eproof.dto.ExamProfileDto;
import com.hkgov.csb.eproof.dto.ExamProfileSummaryDto;
import com.hkgov.csb.eproof.dto.ExamProfileUpdateDto;
import com.hkgov.csb.eproof.entity.ExamProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ExamProfileService {

    Boolean create(ExamProfileCreateDto request);

    void freeze(String examProfileSerialNo);

    void unfreeze(String examProfileSerialNo);

    void update(String id, ExamProfileUpdateDto requestDto);

    ExamProfileDto getExamProfileInfo(String examProfileSerialNo);

    Page<ExamProfile> list(Pageable pageable,String keyWord);

    List<ExamProfile> dropDown();

    Boolean delete(String examProfileSerialNo);

    ExamProfileSummaryDto getSummary(String examProfileSerialNo);

    void reset(String examProfileSerialNo) throws Exception;
}
