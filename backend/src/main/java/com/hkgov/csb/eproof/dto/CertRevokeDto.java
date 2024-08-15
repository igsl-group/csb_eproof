package com.hkgov.csb.eproof.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CertRevokeDto {
    private String remark;
    private String emailTarget;
    private String emailContent;

    String type;
    String hkid;
    String name;
    List<CertInfoDto> certInfos;
}
