package com.hkgov.csb.eproof.util;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.config.ConfigureBuilder;
import com.deepoove.poi.plugin.table.LoopRowTableRenderPolicy;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.hkgov.csb.eproof.constants.Constants;
import com.hkgov.csb.eproof.constants.enums.ExceptionEnums;
import com.hkgov.csb.eproof.exception.GenericException;
import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.docx4j.Docx4J;
import org.docx4j.model.fields.merge.DataFieldName;
import org.docx4j.model.fields.merge.MailMerger;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class DocxUtil {

    private final ObjectMapper objectMapper;

    @Value("${document.generate-temp-path}")
    private String tempDocumentPath;
    @Value("${document.libreoffice-program-path}")
    private String libreOfficeProgramPath;


    Logger logger = LoggerFactory.getLogger(this.getClass());
    public DocxUtil(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    public byte[] getMergedDocumentBinary(InputStream inputStream,Map<DataFieldName, String> fieldMergeMap ) throws Docx4JException, IOException {
        // Create a new inputstream to prevent WordprocessingMLPackage closing the original inputstream
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(inputStream);
        IOUtils.close(inputStream);
        wordMLPackage.getMainDocumentPart();

        MailMerger.setMERGEFIELDInOutput(MailMerger.OutputField.REMOVED);

        MailMerger.performMerge(wordMLPackage, fieldMergeMap, true);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        wordMLPackage.save(baos);
        baos.close();

        return baos.toByteArray();
    }
    @SafeVarargs
    public final Map<DataFieldName, String> combineMapsToFieldMergeMap(Map<String, String>... mergeMaps){
        Map<DataFieldName, String> fieldMergeMap = new HashMap<>();
        if(mergeMaps.length > 0){
            for (Map<String, String> loopMap : mergeMaps) {
                for(Map.Entry<String, String> entry:loopMap.entrySet()){
                    fieldMergeMap.put(new DataFieldName(entry.getKey()),entry.getValue());
                }
            }
        }
        return fieldMergeMap;
    }

    public File createTempDocxFile(byte [] docxBinary) throws IOException {

        if(!Files.exists(Paths.get(tempDocumentPath))){
            Files.createDirectories(Paths.get(tempDocumentPath));
        }
        String randomFileName = this.generateRandomFileName();
        logger.info("Random file name: {}",randomFileName);

        String docxLocation = String.format("%s/%s.docx",tempDocumentPath,randomFileName);


        logger.info("Generated TEMP DOCX path: {}",docxLocation);

        FileOutputStream docxFos = new FileOutputStream(docxLocation);
        docxFos.write(docxBinary);
        docxFos.close();

        return new File(docxLocation);
    }
    public byte [] convertDocxToPdf2(byte[] docxFileBinary) throws Docx4JException, IOException {
        InputStream is = new ByteArrayInputStream(docxFileBinary);
        WordprocessingMLPackage wordprocessingMLPackage = WordprocessingMLPackage.load(is);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Docx4J.toPDF(wordprocessingMLPackage,baos);
        baos.close();
        return baos.toByteArray();
    }

    public byte [] convertDocxToPdf_POI(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        XWPFDocument document = new XWPFDocument(is);
        PdfOptions options = PdfOptions.create();
        PdfConverter.getInstance().convert(document, baos, options);
        baos.close();
        
        return baos.toByteArray();
    }

    public byte [] convertDocxToPdf(File docxFile) throws IOException, InterruptedException {

        String pdfLocation = String.format("%s/%s.pdf",docxFile.getParent(), FilenameUtils.getBaseName(docxFile.getAbsolutePath()));


        String libreConversionCommand = "";
        Process process = null;

        if (SystemUtils.IS_OS_WINDOWS) {
            // Current OS is Windows
            libreConversionCommand = "\"%s/soffice\" --headless --convert-to pdf \"%s\" --outdir \"%s\""
                    .formatted(libreOfficeProgramPath, docxFile.getAbsolutePath(),tempDocumentPath);
            process = Runtime.getRuntime().exec(new String[]{libreConversionCommand});
        } else if (SystemUtils.IS_OS_LINUX) {
            // Current OS is Linux
            String[] command = new String[]{libreOfficeProgramPath + "/soffice", "--convert-to", "pdf", docxFile.getAbsolutePath(), "--outdir", tempDocumentPath};
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.directory(new File(libreOfficeProgramPath).getParentFile());
            process = processBuilder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                 BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
                while ((line = errorReader.readLine()) != null) {
                    System.err.println(line);
                }
            }
        }

        int exitCode = process.waitFor();
        logger.debug("Command executed with exit code: " + exitCode);
        if(exitCode != 0){
            // Execution error
            //TODO Print error message
            throw new GenericException(ExceptionEnums.DOCUMENT_MERGE_ERROR);
        }

        byte[] pdfBinary = Files.readAllBytes(Paths.get(pdfLocation));
        logger.info("Generated TEMP PDF path: {}", pdfLocation);

//        this.deleteFile(docxLocation);
//        this.deleteFile(pdfLocation);

        return pdfBinary;
    }

    /*public byte [] convertDocxToPdf(byte[] docxBinary) throws IOException, InterruptedException {
        String randomFileName = this.generateRandomFileName();

        String docxLocation = String.format("%s\\%s.docx",tempDocumentPath,randomFileName);
        String pdfLocation = String.format("%s\\%s.pdf",tempDocumentPath,randomFileName);

        logger.info("Random file name: {}",randomFileName);

        FileOutputStream docxFos = new FileOutputStream(docxLocation);
        docxFos.write(docxBinary);
        docxFos.close();

        File file = new File(docxLocation);

        String libreConversionCommand = "";

        if (SystemUtils.IS_OS_WINDOWS) {
            // Current OS is Windows
            libreConversionCommand = "\"%s\\soffice\" --headless --convert-to pdf \"%s\" --outdir \"%s\""
                    .formatted(libreOfficeProgramPath,docxLocation,tempDocumentPath);
        } else if (SystemUtils.IS_OS_LINUX) {
            // Current OS is Linux
            libreConversionCommand ="sudo soffice --convert-to pdf \"%s\" --outdir \"%s\"".formatted(docxLocation,tempDocumentPath);
        }
        Process process = Runtime.getRuntime().exec(new String[]{libreConversionCommand});
        int exitCode = process.waitFor();
        System.out.println("Command executed with exit code: " + exitCode);
        if(exitCode != 0){
            // Execution error
            //TODO Print error message
            throw new GenericException(ExceptionEnums.DOCUMENT_MERGE_ERROR);
        }

        byte[] pdfBinary = Files.readAllBytes(Paths.get(pdfLocation));

        this.deleteFile(docxLocation);
        this.deleteFile(pdfLocation);

        return pdfBinary;
    }*/

    public byte [] processTableLoopRender(InputStream is, Map<String, List> loopMap) throws IOException {

        LoopRowTableRenderPolicy policy = new LoopRowTableRenderPolicy();
        ConfigureBuilder builder = Configure.builder();
        for(var entry:loopMap.entrySet()){
            builder.bind(entry.getKey(),policy);
        }
        Configure config = builder.build();
        XWPFTemplate template = XWPFTemplate.compile(is,config).render(loopMap);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        template.write(baos);
        baos.close();

        return baos.toByteArray();
    }

   /* public byte [] processTableLoopRender(InputStream inputStream, Map<String, List> loopMap) throws IOException {
        String randomFileName = this.generateRandomFileName();

        String docxLocation = String.format("%s\\%s.docx",tempDocumentPath,randomFileName);

        logger.info("Random file name: {}",randomFileName);

        FileOutputStream docxFos = new FileOutputStream(docxLocation);
        docxFos.write(IOUtils.toByteArray(inputStream));
        docxFos.close();

        inputStream.close();

        LoopRowTableRenderPolicy policy = new LoopRowTableRenderPolicy();
        ConfigureBuilder builder = Configure.builder();
        for(var entry:loopMap.entrySet()){
            builder.bind(entry.getKey(),policy);
        }
        Configure config = builder.build();
        XWPFTemplate template = XWPFTemplate.compile(docxLocation,config).render(loopMap);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        template.write(baos);
        this.deleteFile(docxLocation);

        return baos.toByteArray();
    }*/

    public void deleteFile(String filePath){
        logger.info("Deleting file: {}",filePath);
        new File(filePath).delete();
    }
    public Map<String, String> convertObjectToMap(Object obj, String prefix) throws JsonProcessingException {
        String jsonString = objectMapper.writeValueAsString(obj);
        JsonNode jsonNode = objectMapper.readTree(jsonString);
        Map<String, String> map = new HashMap<>();
        this.populateMap(jsonNode, prefix, map);
        return map;
    }

    private void populateMap(JsonNode jsonNode, String prefix, Map<String, String> map) {
        if (jsonNode.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fieldsIterator = jsonNode.fields();
            while (fieldsIterator.hasNext()) {
                Map.Entry<String, JsonNode> entry = fieldsIterator.next();
                String fieldName = entry.getKey();
                JsonNode fieldValue = entry.getValue();
                String mapKey = (prefix.isEmpty() ? "" : prefix + ".") + fieldName;

                if (fieldValue.isObject()) {
                    populateMap(fieldValue, mapKey, map);
                } else {
                    map.put(mapKey, jsonNode.isNull()? "" : fieldValue.asText());
                }
            }
        }
    }

    private String generateRandomFileName(){
        // first string: Current time
        // second string: UUID
        return String.format("%s_%s",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(Constants.DATE_TIME_PATTERN_2)),
                UUID.randomUUID()
        );
    }

}

