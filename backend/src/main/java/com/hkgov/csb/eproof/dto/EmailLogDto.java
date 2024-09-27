package com.hkgov.csb.eproof.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import static com.hkgov.csb.eproof.constants.Constants.DATE_TIME_PATTERN;

@Getter
@Setter
public class EmailLogDto {
    private Long id;
    private String subject;
    private String to;
    private String body;
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    LocalDateTime createdDate;
}
