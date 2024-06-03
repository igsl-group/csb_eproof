package com.hkgov.csb.eproof.service;


import com.hkgov.csb.eproof.dto.ExamProfileDto;
import com.hkgov.csb.eproof.dto.ExamProfileSummaryDto;
import com.hkgov.csb.eproof.entity.ExamProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ExamProfileService {

    Boolean create(ExamProfileDto request);

    Boolean freeze(String examProfileSerialNo);

    ExamProfile getexamProfileInfo(String examProfileSerialNo);

    Page<ExamProfile> list(Pageable pageable,String keyWord);

    List<ExamProfile> dropDown();

    Boolean delete(String examProfileSerialNo);

    ExamProfileSummaryDto getSummary(String examProfileSerialNo);
}
