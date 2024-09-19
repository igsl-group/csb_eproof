package com.hkgov.csb.eproof.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateHisApproveDto {
    private Long historicalResultId;
    private Boolean oldBlVoid;
    private Boolean newBlVoid;
    private Boolean oldUeVoid;
    private Boolean new_ue_void;
    private Boolean oldUcVoid;
    private Boolean newUcVoid;
    private Boolean oldAtVoid;
    private Boolean newAtVoid;
    private Boolean oldValid;
    private Boolean newValid;
    private String remark;
}
