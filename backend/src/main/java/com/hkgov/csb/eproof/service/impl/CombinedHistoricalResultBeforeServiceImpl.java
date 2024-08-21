package com.hkgov.csb.eproof.service.impl;

import com.hkgov.csb.eproof.constants.enums.ExceptionEnums;
import com.hkgov.csb.eproof.dao.CombinedHistoricalResultBeforeRepository;
import com.hkgov.csb.eproof.dto.UpdateHistoricalDto;
import com.hkgov.csb.eproof.entity.CombinedHistoricalResultBefore;
import com.hkgov.csb.eproof.exception.GenericException;
import com.hkgov.csb.eproof.service.CombinedHistoricalResultBeforeService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Objects;

/**
* @author David
* @description 针对表【combined_historical_result_before_2024】的数据库操作Service实现
* @createDate 2024-08-16 15:53:54
*/
@Service
@RequiredArgsConstructor
public class CombinedHistoricalResultBeforeServiceImpl implements CombinedHistoricalResultBeforeService {
    private final CombinedHistoricalResultBeforeRepository repository;
    @Override
    public Page<CombinedHistoricalResultBefore> list(Pageable pageable, String keyword) {
        return repository.findPage(pageable,keyword);
    }

    @Override
    public void valid(Long id, String remark) {
        CombinedHistoricalResultBefore before = repository.findById(id).orElse(null);
        if(Objects.isNull(before)){
            throw new GenericException(ExceptionEnums.EXAM_PROFILE_NOT_EXIST);
        }
        before.setValid(true);
        before.setRemark(remark);
        repository.save(before);
    }

    @Override
    public void invalid(Long id, String remark) {
        CombinedHistoricalResultBefore before = repository.findById(id).orElse(null);
        if(Objects.isNull(before)){
            throw new GenericException(ExceptionEnums.EXAM_PROFILE_NOT_EXIST);
        }
        before.setValid(false);
        before.setRemark(remark);
        repository.save(before);
    }

    @Override
    public void updateGrade(Long id, UpdateHistoricalDto dto) {
        CombinedHistoricalResultBefore before = repository.findById(id).orElse(null);
        if(Objects.isNull(before)){
            throw new GenericException(ExceptionEnums.EXAM_PROFILE_NOT_EXIST);
        }
        if(StringUtils.isBlank(dto.getSubject())){
            throw new GenericException(ExceptionEnums.SUBJECT_IS_NULL);
        }
        before.setRemark(dto.getRemark());
        switch (dto.getSubject()){
            case "ue_grade" :
                before.setBlDate(LocalDate.now());
                before.setBlVoid(dto.getValid());
                break;
            case "uc_grade" :
                before.setUcDate(LocalDate.now());
                before.setUcVoid(dto.getValid());
                break;
            case "blnst_grade" :
                before.setBlDate(LocalDate.now());
                before.setBlVoid(dto.getValid());
                break;
            case "at_grade" :
                before.setAtDate(LocalDate.now());
                before.setAtVoid(dto.getValid());
                break;
            default:break;
        }
        repository.save(before);
    }
}
