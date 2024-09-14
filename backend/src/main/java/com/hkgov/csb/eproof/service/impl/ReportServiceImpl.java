package com.hkgov.csb.eproof.service.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import com.hkgov.csb.eproof.dao.CertInfoRepository;
import com.hkgov.csb.eproof.dto.ExamResultReportByYearDTO;
import com.hkgov.csb.eproof.dto.ExamResultReportDTO;
import com.hkgov.csb.eproof.service.ReportService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final CertInfoRepository certInfoRepository;

    @Override
    public ByteArrayInputStream createExamResultReport(LocalDate startDate,
            LocalDate endDate) throws IOException {
        List<Object[]> results =
                certInfoRepository.findReportData(startDate, endDate);
        List<ExamResultReportDTO> reportData = results.stream()
                .map(row -> new ExamResultReportDTO((String) row[0],
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
                        ((Number) row[11]).doubleValue(),
                        ((Number) row[12]).doubleValue()))
                .collect(Collectors.toList());

        try (Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Exam Result Report");

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Exam Profile Serial", "UC Total Candidate",
                    "UC No of L2", "UC No of L1", "UE Total Candidate",
                    "UE No of L2", "UE No of L1", "AT Total Candidate",
                    "AT Pass Rate", "AT Fail Rate", "BLNST Total Candidate",
                    "BLNST Pass Rate", "BLNST Fail Rate"};

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // Fill data
            int rowIndex = 1;
            for (ExamResultReportDTO dto : reportData) {
                Row row = sheet.createRow(rowIndex++);

                row.createCell(0).setCellValue(dto.getExamProfileSerial());
                row.createCell(1).setCellValue(dto.getUcTotalCandidate());
                row.createCell(2).setCellValue(dto.getUcNoOfL2());
                row.createCell(3).setCellValue(dto.getUcNoOfL1());
                row.createCell(4).setCellValue(dto.getUeTotalCandidate());
                row.createCell(5).setCellValue(dto.getUeNoOfL2());
                row.createCell(6).setCellValue(dto.getUeNoOfL1());
                row.createCell(7).setCellValue(dto.getAtTotalCandidate());
                row.createCell(8).setCellValue(dto.getAtPassRate());
                row.createCell(9).setCellValue(dto.getAtFailRate());
                row.createCell(10).setCellValue(dto.getBlnstTotalCandidate());
                row.createCell(11).setCellValue(dto.getBlnstPassRate());
                row.createCell(12).setCellValue(dto.getBlnstFailRate());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    @Override
    public ByteArrayInputStream createExamResultByYearReport(int year) throws IOException {
        System.out.println("Requested Year: " + year);
        List<Object[]> results =
                certInfoRepository.findReportData(year);
        
        List<ExamResultReportByYearDTO> reportData = results.stream()
                .map(row -> new ExamResultReportByYearDTO(row[0].toString(),
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
                        ((Number) row[11]).doubleValue(),
                        ((Number) row[12]).doubleValue()))
                .collect(Collectors.toList());

        try (Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Exam Result Report");

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Year", "UC Total Candidate",
                    "UC No of L2", "UC No of L1", "UE Total Candidate",
                    "UE No of L2", "UE No of L1", "AT Total Candidate",
                    "AT Pass Rate", "AT Fail Rate", "BLNST Total Candidate",
                    "BLNST Pass Rate", "BLNST Fail Rate"};

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // Fill data
            int rowIndex = 1;
            for (ExamResultReportByYearDTO dto : reportData) {
                Row row = sheet.createRow(rowIndex++);

                row.createCell(0).setCellValue(dto.getYear());
                row.createCell(1).setCellValue(dto.getUcTotalCandidate());
                row.createCell(2).setCellValue(dto.getUcNoOfL2());
                row.createCell(3).setCellValue(dto.getUcNoOfL1());
                row.createCell(4).setCellValue(dto.getUeTotalCandidate());
                row.createCell(5).setCellValue(dto.getUeNoOfL2());
                row.createCell(6).setCellValue(dto.getUeNoOfL1());
                row.createCell(7).setCellValue(dto.getAtTotalCandidate());
                row.createCell(8).setCellValue(dto.getAtPassRate());
                row.createCell(9).setCellValue(dto.getAtFailRate());
                row.createCell(10).setCellValue(dto.getBlnstTotalCandidate());
                row.createCell(11).setCellValue(dto.getBlnstPassRate());
                row.createCell(12).setCellValue(dto.getBlnstFailRate());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

}
