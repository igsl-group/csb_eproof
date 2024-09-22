package com.hkgov.csb.eproof.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateHisApproveDto {
    private Long id;
    private Long historicalResultId;
    private Boolean oldBlVoid;
    private Boolean newBlVoid;
    private Boolean oldUeVoid;
    private Boolean newUeVoid;
    private Boolean oldUcVoid;
    private Boolean newUcVoid;
    private Boolean oldAtVoid;
    private Boolean newAtVoid;
    private Boolean oldValid;
    private Boolean newValid;
    private String remark;
    private String status;
    private HistoricalResultDto historicalResult;
}
