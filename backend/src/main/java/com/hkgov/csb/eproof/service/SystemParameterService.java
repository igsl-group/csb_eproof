package com.hkgov.csb.eproof.service;

import com.hkgov.csb.eproof.dto.SystemParameterUpDto;
import com.hkgov.csb.eproof.entity.SystemParameter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
* @author 20768
* @description 针对表【system_parameter】的数据库操作Service
* @createDate 2024-07-09 14:24:09
*/
public interface SystemParameterService{
    Page<SystemParameter> list(Pageable pageable, String keyWord);
    void update(Long id, SystemParameterUpDto param);
}
