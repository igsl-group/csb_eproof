package com.hkgov.csb.eproof.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailTemplateDto {

    private Long id;

    private String templateName;

    private String subject;

    private String body;

    private String type;

    private String includeEmails;
}
