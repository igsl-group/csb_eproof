package com.hkgov.csb.eproof.service;


import java.io.IOException;
import java.io.InputStream;


public interface LetterTemplateService {
    InputStream getTemplateByNameAsInputStream(String templateName);
    byte[]  getTemplateByNameAsByteArray(String templateName) throws IOException;
}
