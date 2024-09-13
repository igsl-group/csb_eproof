package com.hkgov.csb.eproof.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;

public interface ReportService {
  
  ByteArrayInputStream createExamResultReport(LocalDate startDate, LocalDate endDate) throws IOException;
  ByteArrayInputStream createExamResultByYearReport(int year) throws IOException;
} 