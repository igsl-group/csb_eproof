package com.hkgov.csb.eproof.constants.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExceptionEnums {
    // Authentication
    ACCESS_DENIED("access.denied","Your access is denied."),

    // Case
    ILLEGAL_SEARCH_TYPE("illegal.search.type","Illegal search type provided."),

    // Document generation
    DOCUMENT_MERGE_ERROR("document.merge.error","Encountered error when merging document."),
    ;


    private final String code;
    private final String message;

}
