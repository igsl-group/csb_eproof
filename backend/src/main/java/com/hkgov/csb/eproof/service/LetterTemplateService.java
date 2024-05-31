package com.hkgov.csb.eproof.service;


import java.io.InputStream;


public interface LetterTemplateService {
    InputStream getTemplateByNameAsInputStream(String templateName);
}
