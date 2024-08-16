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
    CERT_INFO_NOT_UPDATE("cert.info.not.update","Current Cert does not allow updates."),
    CERT_INFO_NOT_DELETE("cert.info.not.delete","Current Cert does not allow deletion."),
    CANNOT_UPLOAD_SIGNED_PDF_FOR_CERT("signed.pdf.upload.error","signed.pdf.upload.error"),
    CERT_EPROOF_EXISTING_RECORD_FOUND("cert.eproof.existing.record.found","Cert Eproof existing record found. Cannot create new cert eproof record."),
    EXAM_INFO_NOT_EXIST("exam.info.not.exist","Exam info does not exist."),

    // Letter template
    TEMPLATE_NAME_CANNOT_BE_NULL("template.name.cannot.null","Not accept null template name."),
    TEMPLATE_NOT_EXIST("template.not.exist","Template does not exist."),

    // Document generation
    DOCUMENT_MERGE_ERROR("document.merge.error","Encountered error when merging document."),


    EXAM_PROFILE_NOT_EXIST("exam.profile.not.exist","Exam profile does not exist."),


    EMAIL_CONTENT_ERROR("email.content.error","Email content conversion failed."),
    SYSTEM_PARAMETER_NOT_EXIST("system.parameter.not.exist","System parameter does not exist."),
    E_PROOF_NOT_FOUND("404","e-Proof document(JSON) not found"),
    E_PROOF_SYSTEM_ERROR("500","Internal Server Error"),
    ;


    private final String code;
    private final String message;

}
