package com.hkgov.csb.eproof.service;

import com.hkgov.csb.eproof.dto.PermissionDto;

import java.util.List;

/**
* @author 20768
* @description 针对表【permission】的数据库操作Service
* @createDate 2024-04-26 17:15:34
*/
public interface PermissionService {

    Boolean creatrePer(PermissionDto requestDto);

    void removePre(String id);

    Boolean updatePre(PermissionDto requestDto);

    List<PermissionDto> getAll();

}
