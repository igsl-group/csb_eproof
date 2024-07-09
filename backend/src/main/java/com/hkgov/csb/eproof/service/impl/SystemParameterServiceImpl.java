package com.hkgov.csb.eproof.service.impl;


import com.hkgov.csb.eproof.constants.enums.ExceptionEnums;
import com.hkgov.csb.eproof.dao.SystemParameterRepository;
import com.hkgov.csb.eproof.dto.SystemParameterUpDto;
import com.hkgov.csb.eproof.entity.SystemParameter;
import com.hkgov.csb.eproof.exception.GenericException;
import com.hkgov.csb.eproof.service.SystemParameterService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
* @author 20768
* @description 针对表【system_parameter】的数据库操作Service实现
* @createDate 2024-07-09 14:24:09
*/
@Service
@RequiredArgsConstructor
public class SystemParameterServiceImpl implements SystemParameterService {
    private final SystemParameterRepository systemParameterRepository;
    @Override
    public Page<SystemParameter> list(Pageable pageable, String keyWord) {
        var systemParam = systemParameterRepository.findPage(pageable,keyWord);
        return systemParam;
    }

    @Override
    public void update(Long id, SystemParameterUpDto param) {
        SystemParameter systemParameter = systemParameterRepository.findById(id).orElse(null);
        if(Objects.isNull(systemParameter)){
            throw new GenericException(ExceptionEnums.SYSTEM_PARAMETER_NOT_EXIST);
        }
        systemParameter.setValue(param.getValue());
        systemParameterRepository.save(systemParameter);
    }
}
