package com.hkgov.csb.eproof.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CertApproveRejectRevokeDto {
    private String remark;
    private String emailTarget;
    private String emailContent;
}
