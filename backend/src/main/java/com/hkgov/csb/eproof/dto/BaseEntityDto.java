package com.hkgov.csb.eproof.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

import static com.hkgov.csb.eproof.config.Constants.DATE_TIME_PATTERN;

public class BaseEntityDto {
    private AuditorDetailsDto modifiedBy;
    private AuditorDetailsDto createdBy;
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime createDate;
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime modifiedDate;

    public AuditorDetailsDto getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(AuditorDetailsDto modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public AuditorDetailsDto getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(AuditorDetailsDto createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(LocalDateTime modifiedDate) {
        this.modifiedDate = modifiedDate;
    }
}
