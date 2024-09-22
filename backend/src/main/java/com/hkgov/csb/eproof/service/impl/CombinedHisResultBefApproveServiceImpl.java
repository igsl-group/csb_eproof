package com.hkgov.csb.eproof.service.impl;


import com.hkgov.csb.eproof.constants.enums.ExceptionEnums;
import com.hkgov.csb.eproof.dao.CombinedHisResultBefApproveRepository;
import com.hkgov.csb.eproof.dao.CombinedHistoricalResultBeforeRepository;
import com.hkgov.csb.eproof.dto.UpdateHisApproveDto;
import com.hkgov.csb.eproof.entity.CombinedHisResultBefApprove;
import com.hkgov.csb.eproof.entity.CombinedHistoricalResultBefore;
import com.hkgov.csb.eproof.entity.enums.CertStatus;
import com.hkgov.csb.eproof.exception.GenericException;
import com.hkgov.csb.eproof.mapper.CombinedHisResultBefApproveMapper;
import com.hkgov.csb.eproof.service.CombinedHisResultBefApproveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
* @description 针对表【combined_historical_result_before_2024_approve】的数据库操作Service实现
* @createDate 2024-09-18 10:38:07
*/
@Service
@RequiredArgsConstructor
public class CombinedHisResultBefApproveServiceImpl implements CombinedHisResultBefApproveService {
    private final CombinedHisResultBefApproveRepository approveRepository;
    private final CombinedHistoricalResultBeforeRepository beforeRepository;
    @Override
    public void update(Long id,UpdateHisApproveDto dto) {
        CombinedHisResultBefApprove record = approveRepository.findById(id).orElse(null);
        if(Objects.isNull(record)){
            throw new GenericException(ExceptionEnums.HISTORICAL_ACHIEVEMENT_NOT_EXIST);
        }
        record.setOldBlVoid(dto.getOldBlVoid());
        record.setNewBlVoid(dto.getNewBlVoid());

        record.setOldUeVoid(dto.getOldUeVoid());
        record.setNewUeVoid(dto.getNewUeVoid());

        record.setOldUcVoid(dto.getOldUcVoid());
        record.setNewUcVoid(dto.getNewUcVoid());

        record.setOldAtVoid(dto.getOldAtVoid());
        record.setNewAtVoid(dto.getNewAtVoid());

        record.setOldValid(dto.getOldValid());
        record.setNewValid(dto.getNewValid());
        record.setRemark(dto.getRemark());
        record.setStatus(CertStatus.PENDING);
        approveRepository.save(record);
    }

    @Override
    public void request(UpdateHisApproveDto dto) {
        CombinedHisResultBefApprove record = CombinedHisResultBefApproveMapper.INSTANCE.sourceToDestination(dto);
        record.setStatus(CertStatus.PENDING);
        approveRepository.save(record);

        CombinedHistoricalResultBefore combinedHistoricalResultBeforeRecord = beforeRepository.findById(dto.getHistoricalResultId()).orElse(null);
        combinedHistoricalResultBeforeRecord.setActionFreeze(true);
        beforeRepository.save(combinedHistoricalResultBeforeRecord);
    }

    @Override
    public void approve(Long id, UpdateHisApproveDto dto) {
        CombinedHisResultBefApprove record = approveRepository.findById(id).orElse(null);
        if(Objects.isNull(record)){
            throw new GenericException(ExceptionEnums.HISTORICAL_ACHIEVEMENT_NOT_EXIST);
        }
        record.setRemark(dto.getRemark());
        record.setStatus(CertStatus.APPROVED);
        CombinedHistoricalResultBefore before = beforeRepository.findById(record.getHistoricalResultId()).orElse(null);
        before.setUeVoid(record.getNewUeVoid());
        before.setUcVoid(record.getNewUcVoid());
        before.setAtVoid(record.getNewAtVoid());
        before.setBlVoid(record.getNewBlVoid());
        before.setValid(record.getNewValid());
        before.setRemark(dto.getRemark());
        before.setActionFreeze(false);
        beforeRepository.save(before);
        approveRepository.save(record);
    }

    @Override
    public void reject(Long id, UpdateHisApproveDto dto) {
        CombinedHisResultBefApprove record = approveRepository.findById(id).orElse(null);
        if(Objects.isNull(record)){
            throw new GenericException(ExceptionEnums.HISTORICAL_ACHIEVEMENT_NOT_EXIST);
        }
        record.setRemark(dto.getRemark());
        record.setStatus(CertStatus.REJECTED);
        approveRepository.save(record);
    }

    @Override
    public List<UpdateHisApproveDto> list() {
        List<UpdateHisApproveDto> list = approveRepository.findByStatus().stream().map(CombinedHisResultBefApproveMapper.INSTANCE::destinationToSource).toList();
        return list;
    }

    @Override
    public void remove(Long id) {
        CombinedHisResultBefApprove record = approveRepository.findById(id).orElse(null);
        if(Objects.isNull(record)){
            throw new GenericException(ExceptionEnums.HISTORICAL_ACHIEVEMENT_NOT_EXIST);
        }
        record.setStatus(CertStatus.WITHDRAWAL);
        approveRepository.save(record);

        CombinedHistoricalResultBefore before = beforeRepository.findById(record.getHistoricalResultId()).orElse(null);
        before.setActionFreeze(false);
        beforeRepository.save(before);
    }


}
