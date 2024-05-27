package com.hkgov.csb.eproof.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.docx4j.Docx4J;

import org.docx4j.model.fields.merge.DataFieldName;
import org.docx4j.model.fields.merge.MailMerger;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.jodconverter.core.DocumentConverter;
import org.jodconverter.core.document.DefaultDocumentFormatRegistry;
import org.springframework.stereotype.Component;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Component
public class DocxUtil {

    private final ObjectMapper objectMapper;

    public DocxUtil(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }



    public byte[] getMergedDocumentBinary(InputStream inputStream,Map<String, String> ... mergeMaps ) throws Docx4JException, IOException {
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(inputStream);

        wordMLPackage.getMainDocumentPart();

        MailMerger.setMERGEFIELDInOutput(MailMerger.OutputField.REMOVED);

        Map<DataFieldName, String> fieldMergeMap = new HashMap<>();
        if(mergeMaps.length > 0){
            for (Map<String, String> loopMap : mergeMaps) {
                for(Map.Entry<String, String> entry:loopMap.entrySet()){
                    fieldMergeMap.put(new DataFieldName(entry.getKey()),entry.getValue());
                }
            }
        }


        MailMerger.performMerge(wordMLPackage, fieldMergeMap, true);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        wordMLPackage.save(baos);

        return baos.toByteArray();
    }



 /*   public byte[] convertDocxToPdf2(InputStream is)throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        documentConverter
                .convert(is)
                .as(DefaultDocumentFormatRegistry.DOCX)
                .to(baos)
                .as(DefaultDocumentFormatRegistry.PDF)
                .execute();
        baos.close();
        return baos.toByteArray();
    }*/


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

}

