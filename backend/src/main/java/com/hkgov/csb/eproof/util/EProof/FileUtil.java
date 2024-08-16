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
import java.util.Objects;

public class FileUtil {
    public static File compressFilesToZip(List<File> csvFiles) throws IOException {
        // Create a temporary ZIP file
        File zipFile = File.createTempFile("data", ".zip");

        try (FileOutputStream fos = new FileOutputStream(zipFile);
             ZipArchiveOutputStream zos = new ZipArchiveOutputStream(fos)) {
            for (File csvFile : csvFiles) {
                // Add each CSV file to the ZIP
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
        File cetInfoCsvFile = CollUtil.isNotEmpty(certInfos) ? createCsvFile("after",certInfos,befores) : null;
        File beforeCsvFile = CollUtil.isNotEmpty(befores) ? createCsvFile("before",certInfos,befores) : null;
        if(Objects.nonNull(cetInfoCsvFile)){
            createCsvFiles.add(cetInfoCsvFile);
        }
        if(Objects.nonNull(beforeCsvFile)){
            createCsvFiles.add(beforeCsvFile);
        }
        if(CollUtil.isEmpty(createCsvFiles)){
            return null;
        }
        File zipFile = compressFilesToZip(createCsvFiles);
        byte[] zipBytes = Files.readAllBytes(zipFile.toPath());
        zipFile.delete();
        deleteFile(cetInfoCsvFile);
        deleteFile(beforeCsvFile);
        return zipBytes;
    }

    public static File createCsvFile(String dataName,List<CertInfo> certInfos, List<CombinedHistoricalResultBefore> befores) throws IOException {
        String fileName ="";
        if(dataName.equals("before")){
            fileName = "2024_Before_Exam_results"+"_" + System.currentTimeMillis();
        }else{
            fileName = "2024_After_Exam_results"+"_" + System.currentTimeMillis();
        }
        File csvFile = File.createTempFile(fileName, ".csv");
        try (FileWriter writer = new FileWriter(csvFile);
             CSVWriter csvWriter = new CSVWriter(writer)){
            if(dataName.equals("after")){
                csvWriter.writeNext(new String[]{"id","Exam Date","Name","Hkid","Passport", "UE Grade","UC Grade", "AT Grade","BLNST Grade"});
                for (int i = 0; i < certInfos.size(); ++i) {
                    CertInfo info = certInfos.get(i);
                    csvWriter.writeNext(new String[]{String.valueOf(i+1),info.getExamDate().toString(),info.getName(),info.getHkid(),
                            info.getPassportNo(),info.getUeGrade(),info.getUcGrade(),info.getAtGrade(),info.getBlnstGrade()
                    });
                }
            }else{
                csvWriter.writeNext(new String[]{"id","Exam Date","Name","Hkid","Passport","UE Grade","UE Date","UC Grade",
                        "UC Date", "AT Grade","AT Date","BLNST Grade","BLNST Date"});
                for (int i = 0; i < befores.size(); ++i) {
                    CombinedHistoricalResultBefore info = befores.get(i);
                    csvWriter.writeNext(new String[]{
                            String.valueOf(i+1),info.getExamDate().toString(),info.getName(),info.getHkid(),info.getPassport(),info.getUeGrade(),
                            info.getUeDate().toString(),info.getUcGrade(),info.getUcDate().toString(),info.getAtGrade(),
                            info.getAtDate().toString(),info.getBlGrade(),info.getBlDate().toString()
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
