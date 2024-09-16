package com.hkgov.csb.eproof.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.hkgov.csb.eproof.dto.ReportDTO;
import com.hkgov.csb.eproof.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {


 private final ReportService reportService;

  @PostMapping("/result")
  
  public ResponseEntity<InputStreamResource> processRequest(
      @RequestBody ReportDTO requestBody) {

    switch (requestBody.getReportType()) {
      case "1":
        // code block
        break;
      case "2":
        // code block
        break;
      case "3":
        // code block
        break;
      case "4":
        // code block
        break;
      case "5":
        // code block
        break;
      case "6":
      try {
        ByteArrayInputStream bais = reportService
            .createPersonalParticularsUpdatedReport(requestBody.getStart(),
                requestBody.getEnd(), requestBody.getCandidateName(),
                requestBody.getHkidNumber(), requestBody.getPassportNumber());
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=personal_articulars_updated_report.xlsx");
        headers.add(HttpHeaders.CONTENT_TYPE,
            MediaType.APPLICATION_OCTET_STREAM_VALUE);
        InputStreamResource resource = new InputStreamResource(bais);
        return ResponseEntity.ok().headers(headers).body(resource);
      } catch (IOException err) {
        err.printStackTrace();
      }
        break;
      case "7":
      try {
        ByteArrayInputStream bais = reportService
            .createResultUpdatedReport(requestBody.getStart(),
                requestBody.getEnd(), requestBody.getCandidateName(),
                requestBody.getHkidNumber(), requestBody.getPassportNumber());
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=result_updated_report.xlsx");
        headers.add(HttpHeaders.CONTENT_TYPE,
            MediaType.APPLICATION_OCTET_STREAM_VALUE);
        InputStreamResource resource = new InputStreamResource(bais);
        return ResponseEntity.ok().headers(headers).body(resource);
      } catch (IOException err) {
        err.printStackTrace();
      }
        break;
      case "8":
        //code block
        break;
      case "9":
        try {
          ByteArrayInputStream bais = reportService.createExamResultReport(
              requestBody.getStart(), requestBody.getEnd());
          HttpHeaders headers = new HttpHeaders();
          headers.add(HttpHeaders.CONTENT_DISPOSITION,
              "attachment; filename=exam_report.xlsx");
          headers.add(HttpHeaders.CONTENT_TYPE,
              MediaType.APPLICATION_OCTET_STREAM_VALUE);
          InputStreamResource resource = new InputStreamResource(bais);
          return ResponseEntity.ok().headers(headers).body(resource);
        } catch (IOException err) {
          err.printStackTrace();
        }
        break;
      case "10":
        // code block
        try {
          ByteArrayInputStream bais = reportService.createExamResultByYearReport(
            Integer.parseInt(requestBody.getYear()));
          HttpHeaders headers = new HttpHeaders();
          headers.add(HttpHeaders.CONTENT_DISPOSITION,
              "attachment; filename=exam_result_report_by_year.xlsx");
          headers.add(HttpHeaders.CONTENT_TYPE,
              MediaType.APPLICATION_OCTET_STREAM_VALUE);
          InputStreamResource resource = new InputStreamResource(bais);
          return ResponseEntity.ok().headers(headers).body(resource);
        } catch (IOException err) {
          err.printStackTrace();
        }
        break;
      default:
        // code block
    }

    return ResponseEntity.ok().headers(new HttpHeaders())
        .body(new InputStreamResource(new ByteArrayInputStream(null)));
  }
}
