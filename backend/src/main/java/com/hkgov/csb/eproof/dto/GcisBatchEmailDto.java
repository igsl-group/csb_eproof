package com.hkgov.csb.eproof.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;

import static com.hkgov.csb.eproof.constants.Constants.DATE_PATTERN;

/**
 * DTO for {@link com.hkgov.csb.eproof.entity.GcisBatchEmail}
 */
@Value
public class GcisBatchEmailDto implements Serializable {
    Long id;
    Long emailTemplateId;
    @JsonFormat(pattern = DATE_PATTERN)
    LocalDateTime scheduleDatetime;
    String status;
}