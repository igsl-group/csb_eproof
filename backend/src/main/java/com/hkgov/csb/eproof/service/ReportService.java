package com.hkgov.csb.eproof.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;

public interface ReportService {

  ByteArrayInputStream createExamResultReport(LocalDate startDate,
      LocalDate endDate) throws IOException;

  ByteArrayInputStream createExamResultByYearReport(int year)
      throws IOException;

  ByteArrayInputStream createPersonalParticularsUpdatedReport(
      LocalDate startDate, LocalDate endDate, String candidateName, String hkid,
      String passport) throws IOException;

  ByteArrayInputStream createResultUpdatedReport(LocalDate startDate,
      LocalDate endDate, String candidateName, String hkid, String passport)
      throws IOException;
}
