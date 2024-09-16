package com.hkgov.csb.eproof.service.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

        private final CertInfoRepository certInfoRepository;

        private final CertInfoRenewRepository certInfoRenewRepository;

        @Override
        public ByteArrayInputStream createExamResultReport(LocalDate startDate,
                        LocalDate endDate) throws IOException {
                List<Object[]> results = certInfoRepository
                                .findReportData(startDate, endDate);
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

                List<Object[]> results = certInfoRenewRepository
                                .findPersonalParticularsData(startDate, endDate,
                                                candidateName, hkid, passport);

                List<CertInfoRenewReportDTO> reportData = results.stream()
                                .map(row -> new CertInfoRenewReportDTO(
                                                row[0] != null ? row[0]
                                                                .toString()
                                                                : null,
                                                row[1] != null ? row[1]
                                                                .toString()
                                                                : null,
                                                row[2] != null ? row[2]
                                                                .toString()
                                                                : null,
                                                row[3] != null ? row[3]
                                                                .toString()
                                                                : null,
                                                row[4] != null ? row[4]
                                                                .toString()
                                                                : null,
                                                row[5] != null ? row[5]
                                                                .toString()
                                                                : null,
                                                row[6] != null ? row[6]
                                                                .toString()
                                                                : null,
                                                row[7] != null ? ((Timestamp) row[7])
                                                                .toLocalDateTime()
                                                                .toLocalDate()
                                                                : null))
                                .collect(Collectors.toList());

                int totalRecords = reportData.size();

                try (XSSFWorkbook workbook = new XSSFWorkbook();
                                ByteArrayOutputStream out =
                                                new ByteArrayOutputStream()) {
                        Sheet sheet = workbook.createSheet("Report");

                        Row headerRow = sheet.createRow(0);
                        String[] headers = {"Candidate Name", "HKID Number",
                                        "Passport Number",
                                        "Personal Particulars Updated",
                                        "Old Value", "New Value", "Remarks",
                                        "Modified Date"};

                        for (int i = 0; i < headers.length; i++) {
                                Cell cell = headerRow.createCell(i);
                                cell.setCellValue(headers[i]);
                        }

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
                                                result.getOldValue());
                                row.createCell(5).setCellValue(
                                                result.getNewValue());
                                row.createCell(6).setCellValue(
                                                result.getRemarks());
                                row.createCell(7).setCellValue(result
                                                .getModifiedDate().toString());
                        }

                        Row totalRow = sheet.createRow(rowIndex);
                        totalRow.createCell(0).setCellValue("Total Number:");
                        totalRow.createCell(1).setCellValue(totalRecords);

                        for (int i = 0; i < headers.length; i++) {
                                sheet.autoSizeColumn(i);
                        }

                        workbook.write(out);
                        return new ByteArrayInputStream(out.toByteArray());
                }
        }

        @Override
        public ByteArrayInputStream createResultUpdatedReport(
                        LocalDate startDate, LocalDate endDate,
                        String candidateName, String hkid, String passport)
                        throws IOException {
                List<Object[]> results = certInfoRenewRepository.findResultData(
                                startDate, endDate, candidateName, hkid,
                                passport);

                List<CertInfoResultRenewReportDTO> reportData = results.stream()
                                .map(row -> new CertInfoResultRenewReportDTO(
                                                row[0] != null ? row[0]
                                                                .toString()
                                                                : null,
                                                row[1] != null ? row[1]
                                                                .toString()
                                                                : null,
                                                row[2] != null ? row[2]
                                                                .toString()
                                                                : null,
                                                row[3] != null ? row[3]
                                                                .toString()
                                                                : null,
                                                row[4] != null ? row[4]
                                                                .toString()
                                                                : null,
                                                row[5] != null ? row[5]
                                                                .toString()
                                                                : null,
                                                row[6] != null ? row[6]
                                                                .toString()
                                                                : null,
                                                row[7] != null ? ((Timestamp) row[7])
                                                                .toLocalDateTime()
                                                                .toLocalDate()
                                                                : null))
                                .collect(Collectors.toList());

                int totalRecords = reportData.size();

                try (XSSFWorkbook workbook = new XSSFWorkbook();
                                ByteArrayOutputStream out =
                                                new ByteArrayOutputStream()) {
                        Sheet sheet = workbook.createSheet("Report");

                        Row headerRow = sheet.createRow(0);
                        String[] headers = {"Candidate Name", "HKID Number",
                                        "Passport Number", "Result Updated",
                                        "Old Value", "New Value", "Remarks",
                                        "Modified Date"};

                        for (int i = 0; i < headers.length; i++) {
                                Cell cell = headerRow.createCell(i);
                                cell.setCellValue(headers[i]);
                        }

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
                                                result.getResult());
                                row.createCell(4).setCellValue(
                                                result.getOldValue());
                                row.createCell(5).setCellValue(
                                                result.getNewValue());
                                row.createCell(6).setCellValue(
                                                result.getRemarks());
                                row.createCell(7).setCellValue(result
                                                .getModifiedDate().toString());
                        }

                        Row totalRow = sheet.createRow(rowIndex);
                        totalRow.createCell(0).setCellValue("Total Number:");
                        totalRow.createCell(1).setCellValue(totalRecords);

                        for (int i = 0; i < headers.length; i++) {
                                sheet.autoSizeColumn(i);
                        }

                        workbook.write(out);
                        return new ByteArrayInputStream(out.toByteArray());
                }
        }
}
