package com.hkgov.csb.eproof.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;

import static com.hkgov.csb.eproof.constants.Constants.DATE_TIME_PATTERN;

/**
 * DTO for {@link com.hkgov.csb.eproof.entity.File}
 */
@Getter
@Setter
public class FileDto implements Serializable {
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    LocalDateTime createdDate;
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    LocalDateTime modifiedDate;
    String modifiedBy;
    String createdBy;
    Long id;
    String status;
    String path;
    String name;
    String type;
}