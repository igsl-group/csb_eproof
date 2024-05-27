package com.hkgov.csb.eproof.service.impl;


import com.hkgov.csb.eproof.constants.Constants;
import com.hkgov.csb.eproof.constants.enums.DocumentOutputType;
import com.hkgov.csb.eproof.constants.enums.ExceptionEnums;
import com.hkgov.csb.eproof.exception.GenericException;
import com.hkgov.csb.eproof.service.DocumentService;
import com.hkgov.csb.eproof.util.DocxUtil;
import jakarta.validation.constraints.NotNull;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;


@Service
public class DocumentServiceImpl implements DocumentService {

    private final DocxUtil docxUtil;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${document.generate_temp_path}")
    private String tempDocumentPath;
    @Value("${document.libreoffice_install_path}")
    private String libreOfficePath;

    public DocumentServiceImpl(DocxUtil docxUtil) {
        this.docxUtil = docxUtil;
    }


    @Override
    public byte[] getMergedDocument(InputStream templateInputStream, @NotNull(message = "Document output type must not be null!") DocumentOutputType outputType, Map<String, String>... mergeMaps) throws IOException, Docx4JException, InterruptedException {

        byte[] mergedDocxBinaryArray = docxUtil.getMergedDocumentBinary(templateInputStream,mergeMaps);

        if(DocumentOutputType.DOCX.equals(outputType)){
            return mergedDocxBinaryArray;
        }else if (DocumentOutputType.PDF.equals(outputType)){

            String randomFileNameString = this.getRandomDocumentNameString();

            String docxLocation = String.format("%s\\%s.docx",tempDocumentPath,randomFileNameString);

            FileOutputStream docxFos = new FileOutputStream(docxLocation);
            docxFos.write(mergedDocxBinaryArray);
            docxFos.close();

            String pdfLocation = String.format("%s\\%s.pdf",tempDocumentPath,randomFileNameString);

            String os = System.getProperty("os.name").toLowerCase();
            String libreConversionCommand = "";

            ProcessBuilder processBuilder = null;
            if (os.contains("win")) {
                // Current OS is Windows
                /*libreConversionCommand ="""
cmd.exe /c start runas /user:Administrator "%s\\soffice --convert-to pdf "%s" --outdir "%s"
""".formatted(libreOfficePath,docxLocation,tempDocumentPath);*/
                String [] libreCommandArray = {
                        "cmd.exe",
                        "/c",
                        "start",
                        "runas",
                        "/user:Administrator",
                        "\"%s\\soffice\"".formatted(libreOfficePath),
                        "--convert-to",
                        "pdf",
                        "\"%s\"".formatted(docxLocation),
                        "--outdir",
                        "\"%s\"".formatted(tempDocumentPath)

                };
                processBuilder = new ProcessBuilder(libreCommandArray);

//                libreConversionCommand = "C:\\Windows\\System32\\cmd.exe /c start runas /user:Administrator \"%s\\soffice --convert-to pdf \"%s\" --outdir \"%s\""
//                        .formatted(libreOfficePath, docxLocation, tempDocumentPath);
            } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
                // Current OS is Linux
                libreConversionCommand ="""
sudo soffice --convert-to pdf "%s" --outdir "%s" 
""".formatted(docxLocation,tempDocumentPath);
            }
            logger.info("Command : {}",String.join(" ",processBuilder.command()));
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                logger.info(line);
            }

            int exitCode = process.waitFor();
            System.out.println("Command executed with exit code: " + exitCode);
            if(exitCode != 0){
                // Execution error
                //TODO Print error message
                throw new GenericException(ExceptionEnums.DOCUMENT_MERGE_ERROR);
            }

            return Files.readAllBytes(Paths.get(pdfLocation));

        }

        return new byte[0];
    }

    private String getRandomDocumentNameString(){
        // first string: Current time
        // second string: UUID
        return String.format("%s_%s",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(Constants.DATE_TIME_PATTERN_2)),
                UUID.randomUUID()
        );
    }
}




