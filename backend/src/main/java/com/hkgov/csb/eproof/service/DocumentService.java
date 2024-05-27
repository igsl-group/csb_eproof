package com.hkgov.csb.eproof.service;


import com.hkgov.csb.eproof.constants.enums.DocumentOutputType;
import org.docx4j.openpackaging.exceptions.Docx4JException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public interface DocumentService {

    byte[] getMergedDocument(InputStream templateInputStream, DocumentOutputType outputType, Map<String, String>... mergeMaps) throws IOException, Docx4JException, InterruptedException;

}
