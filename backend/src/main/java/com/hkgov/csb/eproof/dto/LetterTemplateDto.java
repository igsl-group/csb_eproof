package com.hkgov.csb.eproof.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.hkgov.csb.eproof.entity.LetterTemplate}
 */
@Getter
@Setter

public class LetterTemplateDto implements Serializable {
    LocalDateTime createdDate;
    LocalDateTime modifiedDate;
    String modifiedBy;
    String createdBy;
    Long id;
    String name;
    Long fileId;
    String description;
}