package com.hkgov.csb.eproof.service.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import com.hkgov.csb.eproof.dao.CertInfoRenewRepository;
import com.hkgov.csb.eproof.dao.CertInfoRepository;
import com.hkgov.csb.eproof.dto.CertInfoRenewReportDTO;
import com.hkgov.csb.eproof.dto.CertInfoResultRenewReportDTO;
import com.hkgov.csb.eproof.dto.ExamResultReportByYearDTO;
import com.hkgov.csb.eproof.dto.ExamResultReportDTO;
import com.hkgov.csb.eproof.service.ReportService;
import com.hkgov.csb.eproof.util.HKIDformatter;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

        private final CertInfoRepository certInfoRepository;

        private final CertInfoRenewRepository certInfoRenewRepository;

        private final HKIDformatter hkidFormatter;

        @Override
        public ByteArrayInputStream createExamResultReport(LocalDate startDate,
                        LocalDate endDate, String examProfileSerialNo)
                        throws IOException {
                List<Object[]> results = certInfoRepository.findReportData(
                                startDate, endDate, examProfileSerialNo);
                List<ExamResultReportDTO> reportData = results.stream()
                                .map(row -> new ExamResultReportDTO(
                                                (String) row[0],
                                                ((Number) row[1]).longValue(),
                                                ((Number) row[2]).doubleValue(),
                                                ((Number) row[3]).doubleValue(),
                                                ((Number) row[4]).longValue(),
                                                ((Number) row[5]).doubleValue(),
                                                ((Number) row[6]).doubleValue(),
                                                ((Number) row[7]).longValue(),
                                                ((Number) row[8]).doubleValue(),
                                                ((Number) row[9]).doubleValue(),
                                                ((Number) row[10]).longValue(),
                                                ((Number) row[11])
                                                                .doubleValue(),
                                                ((Number) row[12])
                                                                .doubleValue()))
                                .collect(Collectors.toList());

                try (Workbook workbook = new XSSFWorkbook();
                                ByteArrayOutputStream out =
                                                new ByteArrayOutputStream()) {
                        Sheet sheet = workbook
                                        .createSheet("Exam Result Report");

                        // Create header row
                        Row headerRow = sheet.createRow(0);
                        String[] headers = {"Exam Profile Serial",
                                        "UC Total Candidate", "UC No of L2",
                                        "UC No of L1", "UE Total Candidate",
                                        "UE No of L2", "UE No of L1",
                                        "AT Total Candidate", "AT Pass Rate",
                                        "AT Fail Rate", "BLNST Total Candidate",
                                        "BLNST Pass Rate", "BLNST Fail Rate"};

                        for (int i = 0; i < headers.length; i++) {
                                Cell cell = headerRow.createCell(i);
                                cell.setCellValue(headers[i]);
                        }


                        CellStyle percentageStyle = workbook.createCellStyle();
                        DataFormat format = workbook.createDataFormat();
                        percentageStyle.setDataFormat(
                                        format.getFormat("0.00%"));
                        // Fill data
                        int rowIndex = 1;
                        for (ExamResultReportDTO dto : reportData) {
                                Row row = sheet.createRow(rowIndex++);

                                row.createCell(0).setCellValue(
                                                dto.getExamProfileSerial());
                                row.createCell(1).setCellValue(
                                                dto.getUcTotalCandidate());

                                Cell ucNoOfL2Cell = row.createCell(2);
                                ucNoOfL2Cell.setCellValue(dto.getUcNoOfL2());
                                ucNoOfL2Cell.setCellStyle(percentageStyle);

                                Cell ucNoOfL1Cell = row.createCell(3);
                                ucNoOfL1Cell.setCellValue(dto.getUcNoOfL1());
                                ucNoOfL1Cell.setCellStyle(percentageStyle);

                                row.createCell(4).setCellValue(
                                                dto.getUeTotalCandidate());

                                Cell ueNoOfL2Cell = row.createCell(5);
                                ueNoOfL2Cell.setCellValue(dto.getUeNoOfL2());
                                ueNoOfL2Cell.setCellStyle(percentageStyle);

                                Cell ueNoOfL1Cell = row.createCell(6);
                                ueNoOfL1Cell.setCellValue(dto.getUeNoOfL1());
                                ueNoOfL1Cell.setCellStyle(percentageStyle);

                                row.createCell(7).setCellValue(
                                                dto.getAtTotalCandidate());


                                Cell atPassRatCell = row.createCell(8);
                                atPassRatCell.setCellValue(dto.getAtPassRate());
                                atPassRatCell.setCellStyle(percentageStyle);

                                Cell atFailRatCell = row.createCell(9);
                                atFailRatCell.setCellValue(dto.getAtFailRate());
                                atFailRatCell.setCellStyle(percentageStyle);

                                row.createCell(10).setCellValue(
                                                dto.getBlnstTotalCandidate());

                                Cell blnstPassRatCell = row.createCell(11);
                                blnstPassRatCell.setCellValue(
                                                dto.getBlnstPassRate());
                                blnstPassRatCell.setCellStyle(percentageStyle);

                                Cell blnstFailRatCell = row.createCell(12);
                                blnstFailRatCell.setCellValue(
                                                dto.getBlnstFailRate());
                                blnstFailRatCell.setCellStyle(percentageStyle);
                        }

                        for (int i = 0; i < headers.length; i++) {
                                sheet.autoSizeColumn(i);
                        }

                        workbook.write(out);
                        return new ByteArrayInputStream(out.toByteArray());
                }
        }

        @Override
        public ByteArrayInputStream createExamResultByYearReport(int year)
                        throws IOException {
                List<Object[]> results =
                                certInfoRepository.findReportData(year);

                List<ExamResultReportByYearDTO> reportData = results.stream()
                                .map(row -> new ExamResultReportByYearDTO(
                                                row[0].toString(),
                                                ((Number) row[1]).longValue(),
                                                ((Number) row[2]).doubleValue(),
                                                ((Number) row[3]).doubleValue(),
                                                ((Number) row[4]).longValue(),
                                                ((Number) row[5]).doubleValue(),
                                                ((Number) row[6]).doubleValue(),
                                                ((Number) row[7]).longValue(),
                                                ((Number) row[8]).doubleValue(),
                                                ((Number) row[9]).doubleValue(),
                                                ((Number) row[10]).longValue(),
                                                ((Number) row[11])
                                                                .doubleValue(),
                                                ((Number) row[12])
                                                                .doubleValue()))
                                .collect(Collectors.toList());

                try (Workbook workbook = new XSSFWorkbook();
                                ByteArrayOutputStream out =
                                                new ByteArrayOutputStream()) {
                        Sheet sheet = workbook
                                        .createSheet("Exam Result Report");

                        // Create header row
                        Row headerRow = sheet.createRow(0);
                        String[] headers = {"Year", "UC Total Candidate",
                                        "UC No of L2", "UC No of L1",
                                        "UE Total Candidate", "UE No of L2",
                                        "UE No of L1", "AT Total Candidate",
                                        "AT Pass Rate", "AT Fail Rate",
                                        "BLNST Total Candidate",
                                        "BLNST Pass Rate", "BLNST Fail Rate"};

                        for (int i = 0; i < headers.length; i++) {
                                Cell cell = headerRow.createCell(i);
                                cell.setCellValue(headers[i]);
                        }

                        CellStyle percentageStyle = workbook.createCellStyle();
                        DataFormat format = workbook.createDataFormat();
                        percentageStyle.setDataFormat(
                                        format.getFormat("0.00%"));

                        // Fill data
                        int rowIndex = 1;
                        for (ExamResultReportByYearDTO dto : reportData) {
                                Row row = sheet.createRow(rowIndex++);

                                row.createCell(0).setCellValue(dto.getYear());
                                row.createCell(1).setCellValue(
                                                dto.getUcTotalCandidate());

                                Cell ucNoOfL2Cell = row.createCell(2);
                                ucNoOfL2Cell.setCellValue(dto.getUcNoOfL2());
                                ucNoOfL2Cell.setCellStyle(percentageStyle);

                                Cell ucNoOfL1Cell = row.createCell(3);
                                ucNoOfL1Cell.setCellValue(dto.getUcNoOfL1());
                                ucNoOfL1Cell.setCellStyle(percentageStyle);

                                row.createCell(4).setCellValue(
                                                dto.getUeTotalCandidate());

                                Cell ueNoOfL2Cell = row.createCell(5);
                                ueNoOfL2Cell.setCellValue(dto.getUeNoOfL2());
                                ueNoOfL2Cell.setCellStyle(percentageStyle);

                                Cell ueNoOfL1Cell = row.createCell(6);
                                ueNoOfL1Cell.setCellValue(dto.getUeNoOfL1());
                                ueNoOfL1Cell.setCellStyle(percentageStyle);

                                row.createCell(7).setCellValue(
                                                dto.getAtTotalCandidate());


                                Cell atPassRatCell = row.createCell(8);
                                atPassRatCell.setCellValue(dto.getAtPassRate());
                                atPassRatCell.setCellStyle(percentageStyle);

                                Cell atFailRatCell = row.createCell(9);
                                atFailRatCell.setCellValue(dto.getAtFailRate());
                                atFailRatCell.setCellStyle(percentageStyle);

                                row.createCell(10).setCellValue(
                                                dto.getBlnstTotalCandidate());

                                Cell blnstPassRatCell = row.createCell(11);
                                blnstPassRatCell.setCellValue(
                                                dto.getBlnstPassRate());
                                blnstPassRatCell.setCellStyle(percentageStyle);

                                Cell blnstFailRatCell = row.createCell(12);
                                blnstFailRatCell.setCellValue(
                                                dto.getBlnstFailRate());
                                blnstFailRatCell.setCellStyle(percentageStyle);
                        }

                        for (int i = 0; i < headers.length; i++) {
                                sheet.autoSizeColumn(i);
                        }

                        workbook.write(out);
                        return new ByteArrayInputStream(out.toByteArray());
                }
        }

        @Override
        public ByteArrayInputStream createPersonalParticularsUpdatedReport(
                        LocalDate startDate, LocalDate endDate,
                        String candidateName, String hkid, String passport)
                        throws IOException {

                // Fetch results from the repository
                List<Object[]> results = certInfoRenewRepository
                                .findPersonalParticularsData(startDate, endDate,
                                                candidateName, hkid, passport);

                // Map results to DTO
                List<CertInfoRenewReportDTO> reportData = results.stream()
                                .map(row -> new CertInfoRenewReportDTO(
                                                row[0] != null ? row[0]
                                                                .toString()
                                                                : null, // Candidate Name
                                                row[1] != null ? hkidFormatter
                                                                .formatHkid(row[1]
                                                                                .toString())
                                                                : null, // HKID Number
                                                row[2] != null ? row[2]
                                                                .toString()
                                                                : null, // Passport Number
                                                row[3] != null ? row[3]
                                                                .toString()
                                                                : null, // Personal Particulars Updated
                                                row[4] != null ? row[4]
                                                                .toString()
                                                                : null, // Old Name
                                                row[5] != null ? hkidFormatter
                                                                .formatHkid(row[5]
                                                                                .toString())
                                                                : null, // Old HKID
                                                row[6] != null ? row[6]
                                                                .toString()
                                                                : null, // Old Passport
                                                row[7] != null ? row[7]
                                                                .toString()
                                                                : null, // Old Email
                                                row[8] != null ? row[8]
                                                                .toString()
                                                                : null, // New Name
                                                row[9] != null ? hkidFormatter
                                                                .formatHkid(row[9]
                                                                                .toString())
                                                                : null, // New HKID
                                                row[10] != null ? row[10]
                                                                .toString()
                                                                : null, // New Passport
                                                row[11] != null ? row[11]
                                                                .toString()
                                                                : null, // New Email
                                                row[12] != null ? row[12]
                                                                .toString()
                                                                : null, // Remarks
                                                row[13] != null ? ((Timestamp) row[13])
                                                                .toLocalDateTime()
                                                                .toLocalDate()
                                                                : null // Modified Date
                                )).collect(Collectors.toList());

                int totalRecords = reportData.size();

                // Create Excel workbook
                try (XSSFWorkbook workbook = new XSSFWorkbook();
                                ByteArrayOutputStream out =
                                                new ByteArrayOutputStream()) {
                        Sheet sheet = workbook.createSheet("Report");

                        // Create header row
                        Row headerRow = sheet.createRow(0);
                        String[] headers = {"Candidate Name", "HKID Number",
                                        "Passport Number",
                                        "Personal Particulars Updated",
                                        "Old Name", "Old HKID", "Old Passport",
                                        "Old Email", "New Name", "New HKID",
                                        "New Passport", "New Email", "Remarks",
                                        "Date of Update"};

                        for (int i = 0; i < headers.length; i++) {
                                Cell cell = headerRow.createCell(i);
                                cell.setCellValue(headers[i]);
                        }

                        // Populate rows with data
                        int rowIndex = 1;
                        for (CertInfoRenewReportDTO result : reportData) {
                                Row row = sheet.createRow(rowIndex++);
                                row.createCell(0).setCellValue(
                                                result.getCandidateName());
                                row.createCell(1).setCellValue(
                                                result.getHkidNumber());
                                row.createCell(2).setCellValue(
                                                result.getPassportNumber());
                                row.createCell(3).setCellValue(result
                                                .getPersonalParticularsUpdated());
                                row.createCell(4).setCellValue(
                                                result.getOldName());
                                row.createCell(5).setCellValue(
                                                result.getOldHkid());
                                row.createCell(6).setCellValue(
                                                result.getOldPassport());
                                row.createCell(7).setCellValue(
                                                result.getOldEmail());
                                row.createCell(8).setCellValue(
                                                result.getNewName());
                                row.createCell(9).setCellValue(
                                                result.getNewHkid());
                                row.createCell(10).setCellValue(
                                                result.getNewPassport());
                                row.createCell(11).setCellValue(
                                                result.getNewEmail());
                                row.createCell(12).setCellValue(
                                                result.getRemarks());
                                row.createCell(13).setCellValue(
                                                result.getModifiedDate() != null
                                                                ? result.getModifiedDate()
                                                                                .toString()
                                                                : "");
                        }

                        // Add total records row
                        Row totalRow = sheet.createRow(rowIndex);
                        totalRow.createCell(0).setCellValue("Total Number:");
                        totalRow.createCell(1).setCellValue(totalRecords);

                        // Auto-size columns
                        for (int i = 0; i < headers.length; i++) {
                                sheet.autoSizeColumn(i);
                        }

                        // Write workbook to output stream
                        workbook.write(out);
                        return new ByteArrayInputStream(out.toByteArray());
                }
        }

        @Override
        public ByteArrayInputStream createResultUpdatedReport(
                        LocalDate startDate, LocalDate endDate,
                        String candidateName, String hkid, String passport)
                        throws IOException {

                // Fetch results from the repository
                List<Object[]> results = certInfoRenewRepository.findResultData(
                                startDate, endDate, candidateName, hkid,
                                passport);

                List<CertInfoResultRenewReportDTO> reportData = results.stream()
                                .map(row -> new CertInfoResultRenewReportDTO(
                                                row[0] != null ? row[0]
                                                                .toString()
                                                                : null, // Candidate Name
                                                row[1] != null ? hkidFormatter.formatHkid(row[1]
                                                .toString())
                                                                : null, // HKID Number
                                                row[2] != null ? row[2]
                                                                .toString()
                                                                : null, // Passport Number
                                                row[3] != null ? row[3]
                                                                .toString()
                                                                : null, // Result Updated
                                                row[4] != null ? ((Date) row[4])
                                                                .toLocalDate()
                                                                : null, // Exam Date
                                                row[5] != null ? row[5]
                                                                .toString()
                                                                : null, // Old AT Grade
                                                row[6] != null ? row[6]
                                                                .toString()
                                                                : null, // Old BL Grade
                                                row[7] != null ? row[7]
                                                                .toString()
                                                                : null, // Old UC Grade
                                                row[8] != null ? row[8]
                                                                .toString()
                                                                : null, // Old UE Grade
                                                row[9] != null ? row[9]
                                                                .toString()
                                                                : null, // New AT Grade
                                                row[10] != null ? row[10]
                                                                .toString()
                                                                : null, // New BL Grade
                                                row[11] != null ? row[11]
                                                                .toString()
                                                                : null, // New UC Grade
                                                row[12] != null ? row[12]
                                                                .toString()
                                                                : null, // New UE Grade
                                                row[13] != null ? row[13]
                                                                .toString()
                                                                : null, // Remarks
                                                row[14] != null ? ((Timestamp) row[14])
                                                                .toLocalDateTime()
                                                                .toLocalDate()
                                                                : null // Modified Date
                                )).collect(Collectors.toList());

                int totalRecords = reportData.size();

                // Create Excel workbook
                try (XSSFWorkbook workbook = new XSSFWorkbook();
                                ByteArrayOutputStream out =
                                                new ByteArrayOutputStream()) {

                        Sheet sheet = workbook.createSheet("Report");

                        // Create header row
                        Row headerRow = sheet.createRow(0);
                        String[] headers = {"Candidate Name", "HKID Number",
                                        "Passport Number", "Result Updated",
                                        "Exam Date", "Old AT Grade",
                                        "Old BL Grade", "Old UC Grade",
                                        "Old UE Grade", "New AT Grade",
                                        "New BL Grade", "New UC Grade",
                                        "New UE Grade", "Remarks",
                                        "Modified Date"};

                        for (int i = 0; i < headers.length; i++) {
                                Cell cell = headerRow.createCell(i);
                                cell.setCellValue(headers[i]);
                        }

                        // Create data rows
                        int rowIndex = 1;
                        for (CertInfoResultRenewReportDTO result : reportData) {
                                Row row = sheet.createRow(rowIndex++);
                                row.createCell(0).setCellValue(
                                                result.getCandidateName());
                                row.createCell(1).setCellValue(
                                                result.getHkidNumber());
                                row.createCell(2).setCellValue(
                                                result.getPassportNumber());
                                row.createCell(3).setCellValue(
                                                result.getResultUpdated());
                                row.createCell(4).setCellValue(result
                                                .getExamDate() != null ? result
                                                                .getExamDate()
                                                                .toString()
                                                                : "");
                                row.createCell(5).setCellValue(
                                                result.getOldAtGrade());
                                row.createCell(6).setCellValue(
                                                result.getOldBlGrade());
                                row.createCell(7).setCellValue(
                                                result.getOldUcGrade());
                                row.createCell(8).setCellValue(
                                                result.getOldUeGrade());
                                row.createCell(9).setCellValue(
                                                result.getNewAtGrade() != null
                                                                ? result.getNewAtGrade()
                                                                : "");
                                row.createCell(10).setCellValue(
                                                result.getNewBlGrade() != null
                                                                ? result.getNewBlGrade()
                                                                : "");
                                row.createCell(11).setCellValue(
                                                result.getNewUcGrade() != null
                                                                ? result.getNewUcGrade()
                                                                : "");
                                row.createCell(12).setCellValue(
                                                result.getNewUeGrade() != null
                                                                ? result.getNewUeGrade()
                                                                : "");
                                row.createCell(13).setCellValue(
                                                result.getRemarks());
                                row.createCell(14).setCellValue(
                                                result.getModifiedDate() != null
                                                                ? result.getModifiedDate()
                                                                                .toString()
                                                                : "");
                        }

                        Row totalRow = sheet.createRow(rowIndex);
                        totalRow.createCell(0).setCellValue("Total Number:");
                        totalRow.createCell(1).setCellValue(totalRecords);

                        for (int i = 0; i < headers.length; i++) {
                                sheet.autoSizeColumn(i);
                        }

                        // Write to output stream
                        workbook.write(out);
                        return new ByteArrayInputStream(out.toByteArray());
                }
        }
}
