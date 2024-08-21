package com.hkgov.csb.eproof.util.EProof;

import cn.hutool.core.collection.CollUtil;
import com.hkgov.csb.eproof.entity.CertInfo;
import com.hkgov.csb.eproof.entity.CombinedHistoricalResultBefore;
import com.opencsv.CSVWriter;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
    public static File compressFilesToZip(List<File> csvFiles) throws IOException {
        File zipFile = File.createTempFile("data", ".zip");
        try (FileOutputStream fos = new FileOutputStream(zipFile);
             ZipArchiveOutputStream zos = new ZipArchiveOutputStream(fos)) {
            for (File csvFile : csvFiles) {
                try (FileInputStream fis = new FileInputStream(csvFile)) {
                    ZipArchiveEntry zipEntry = new ZipArchiveEntry(csvFile.getName());
                    zos.putArchiveEntry(zipEntry);
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                    }
                    zos.closeArchiveEntry();
                }
            }
        }
        return zipFile;
    }

    public static byte[] createCsvZip(List<CertInfo> certInfos, List<CombinedHistoricalResultBefore> befores) throws IOException {
        List<File> createCsvFiles = new ArrayList<>();
        File cetInfoCsvFile = createCsvFile("after",certInfos,befores);
        File beforeCsvFile = createCsvFile("before",certInfos,befores);
        createCsvFiles.add(cetInfoCsvFile);
        createCsvFiles.add(beforeCsvFile);
        File zipFile = compressFilesToZip(createCsvFiles);
        byte[] zipBytes = Files.readAllBytes(zipFile.toPath());
        zipFile.delete();
        deleteFile(cetInfoCsvFile);
        deleteFile(beforeCsvFile);
        return zipBytes;
    }

    public static File createCsvFile(String dataName,List<CertInfo> afters, List<CombinedHistoricalResultBefore> befores) throws IOException {
        String fileName ="";
        if("before".equals(dataName)){
            fileName = "2024_Before_Exam_results_";
        }else{
            fileName = "2024_After_Exam_results_";
        }
        File csvFile = File.createTempFile(fileName, ".csv");

        try (FileWriter writer = new FileWriter(csvFile);
             CSVWriter csvWriter = new CSVWriter(writer)){
            if("after".equals(dataName)){
                csvWriter.writeNext(new String[]{"id","Exam Date","Name","Hkid","Passport", "UE Grade","UC Grade", "AT Grade","BLNST Grade"});
                if(CollUtil.isNotEmpty(afters)){
                    for (int i = 0; i < afters.size(); ++i) {
                        CertInfo info = afters.get(i);
                        csvWriter.writeNext(new String[]{String.valueOf(i+1),info.getExamDate()==null?null:info.getExamDate().toString(),info.getName(),info.getHkid(),
                                info.getPassportNo(),info.getUeGrade(),info.getUcGrade(),info.getAtGrade(),info.getBlnstGrade()
                        });
                    }
                }
            }
            if("before".equals(dataName)){
                csvWriter.writeNext(new String[]{"id","Name","Hkid","Passport","UE Grade","UE Date","UC Grade",
                        "UC Date", "AT Grade","AT Date","BLNST Grade","BLNST Date"});
                if(CollUtil.isNotEmpty(befores)) {
                    for (int i = 0; i < befores.size(); ++i) {
                        CombinedHistoricalResultBefore info = befores.get(i);
                        csvWriter.writeNext(new String[]{
                                String.valueOf(i+1),info.getName(), info.getHkid(),info.getPassport(),
                                info.getUeGrade(), info.getUeDate()==null?null:info.getUeDate().toString(),
                                info.getUcGrade(), info.getUcDate()==null?null:info.getUcDate().toString(),
                                info.getAtGrade(), info.getAtDate()==null?null:info.getAtDate().toString(),
                                info.getBlGrade(), info.getBlDate()==null?null:info.getBlDate().toString()
                        });
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return csvFile;
    }

    public static void deleteFile(File file) {
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files != null) {
                    for (File f : files) {
                        deleteFile(f);
                    }
                }
            }
            file.delete();
        }
    }
}
