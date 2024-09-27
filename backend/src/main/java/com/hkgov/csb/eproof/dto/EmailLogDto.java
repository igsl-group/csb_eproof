package com.hkgov.csb.eproof.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class EmailLogDto {
    private Long id;
    private String subject;
    private String emailTo;
    private String body;
    private LocalDateTime dateTime;
}
