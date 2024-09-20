package com.hkgov.csb.eproof.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hkgov.csb.eproof.constants.Constants;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ManualResendBatchEmailRequest {
    Long gcisBatchEmailId;
    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    String scheduleTime;
}

