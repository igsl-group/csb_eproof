package com.hkgov.csb.eproof.service;


import com.hkgov.csb.eproof.constants.enums.DocumentOutputType;
import jakarta.validation.constraints.NotNull;
import org.docx4j.model.fields.merge.DataFieldName;
import org.docx4j.openpackaging.exceptions.Docx4JException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface DocumentGenerateService {


    byte[] getMergedDocument(InputStream docxTemplate,
                             @NotNull(message = "Document output type must not be null!") DocumentOutputType outputType,
                             Map<DataFieldName, String> mergeMaps,
                             Map<String, List> tableLoopMap) throws IOException, Docx4JException, InterruptedException;
}
