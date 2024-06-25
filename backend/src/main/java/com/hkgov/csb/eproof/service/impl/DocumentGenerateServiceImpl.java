package com.hkgov.csb.eproof.service.impl;


import com.hkgov.csb.eproof.constants.enums.DocumentOutputType;
import com.hkgov.csb.eproof.service.DocumentGenerateService;
import com.hkgov.csb.eproof.util.DocxUtil;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.io.IOUtils;
import org.docx4j.model.fields.merge.DataFieldName;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;


@Service
public class DocumentGenerateServiceImpl implements DocumentGenerateService {

    private final DocxUtil docxUtil;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    public DocumentGenerateServiceImpl(DocxUtil docxUtil) {
        this.docxUtil = docxUtil;
    }


    public byte[] getMergedDocument(InputStream docxTemplate,
                                    @NotNull(message = "Document output type must not be null!") DocumentOutputType outputType,
                                    Map<DataFieldName, String> mergeMaps,
                                    Map<String, List> tableLoopMap) throws IOException, Docx4JException, InterruptedException {

        logger.info("Start mail merge.");
        byte[] mergedDocxBinaryArray = docxUtil.getMergedDocumentBinary(docxTemplate,mergeMaps);
        IOUtils.close(docxTemplate);
        logger.info("Finish mail merge.");
        File tempDocxFile = docxUtil.createTempDocxFile(mergedDocxBinaryArray);

        logger.info("Start table loop.");
        if (tableLoopMap != null && !tableLoopMap.isEmpty()){
            mergedDocxBinaryArray = docxUtil.processTableLoopRender(new ByteArrayInputStream(mergedDocxBinaryArray),tableLoopMap);

            FileOutputStream fos = new FileOutputStream(tempDocxFile);
            fos.write(mergedDocxBinaryArray);
            fos.close();
        }
        logger.info("Finish table loop.");



        byte[] returnBinary = null;

        switch (outputType){
            case DOCX -> {
                returnBinary = mergedDocxBinaryArray;
            }
            case PDF ->{
                logger.info("Start convert pdf.");
//                returnBinary = docxUtil.convertDocxToPdf(tempDocxFile);
//                returnBinary = docxUtil.convertDocxToPdf2(mergedDocxBinaryArray);
                returnBinary = docxUtil.convertDocxToPdf2(mergedDocxBinaryArray);
                logger.info("Finish convert pdf.");
            }
        }

        docxUtil.deleteFile(tempDocxFile.getAbsolutePath());
        return returnBinary;
    }

}




