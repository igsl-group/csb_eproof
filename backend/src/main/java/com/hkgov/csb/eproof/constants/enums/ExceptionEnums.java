package com.hkgov.csb.eproof.constants.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExceptionEnums {

    // Common error
    SYSTEM_ERROR("system.error","System encountered error."),


    // Authentication
    ACCESS_DENIED("access.denied","Your access is denied."),

    // Cert info
    ILLEGAL_SEARCH_TYPE("illegal.search.type","Illegal search type provided."),
    CERT_INFO_VOIDED("cert.info.voided","Certificate is not valid."),
    CERT_NOT_EXIST("cert.not.exist","Certificate does not exist."),

    // Letter template
    TEMPLATE_NAME_CANNOT_BE_NULL("template.name.cannot.null","Not accept null template name."),
    TEMPLATE_NOT_EXIST("template.not.exist","Template does not exist."),

    // Document generation
    DOCUMENT_MERGE_ERROR("document.merge.error","Encountered error when merging document."),


    EXAM_PROFILE_NOT_EXIST("exam.profile.not.exist","Exam profile does not exist."),
    CERT_INFO_NOT_UPDATE("cert.info.not.update","Current Cert does not allow updates."),
    CERT_INFO_NOT_DELETE("cert.info.not.delete","Current Cert does not allow deletion."),

    EMAIL_CONTENT_ERROR("email.content.error","Email content conversion failed."),
    //system
    SYSTEM_PARAMETER_NOT_EXIST("system.parameter.not.exist","System parameter does not exist.")
    ;


    private final String code;
    private final String message;

}
