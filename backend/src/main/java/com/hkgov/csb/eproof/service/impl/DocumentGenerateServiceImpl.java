package com.hkgov.csb.eproof.service.impl;


import com.hkgov.csb.eproof.constants.enums.DocumentOutputType;
import com.hkgov.csb.eproof.service.DocumentGenerateService;
import com.hkgov.csb.eproof.util.DocxUtil;
import jakarta.validation.constraints.NotNull;
import org.docx4j.model.fields.merge.DataFieldName;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;
import java.util.Map;


@Service
public class DocumentGenerateServiceImpl implements DocumentGenerateService {

    private final DocxUtil docxUtil;

    public DocumentGenerateServiceImpl(DocxUtil docxUtil) {
        this.docxUtil = docxUtil;
    }


    public byte[] getMergedDocument(InputStream docxTemplate,
                                    @NotNull(message = "Document output type must not be null!") DocumentOutputType outputType,
                                    Map<DataFieldName, String> mergeMaps,
                                    Map<String, List> tableLoopMap) throws IOException, Docx4JException, InterruptedException {

        byte[] mergedDocxBinaryArray = docxUtil.getMergedDocumentBinary(docxTemplate,mergeMaps);
        File tempDocxFile = docxUtil.createTempDocxFile(mergedDocxBinaryArray);

        if (tableLoopMap != null && !tableLoopMap.isEmpty()){
            mergedDocxBinaryArray = docxUtil.processTableLoopRender(tempDocxFile,tableLoopMap);

            FileOutputStream fos = new FileOutputStream(tempDocxFile);
            fos.write(mergedDocxBinaryArray);
            fos.close();
        }


        byte[] returnBinary = null;

        switch (outputType){
            case DOCX -> {
                returnBinary = mergedDocxBinaryArray;
            }
            case PDF ->{
                returnBinary = docxUtil.convertDocxToPdf(tempDocxFile);
            }
        }

        docxUtil.deleteFile(tempDocxFile.getAbsolutePath());
        return returnBinary;
    }

}




