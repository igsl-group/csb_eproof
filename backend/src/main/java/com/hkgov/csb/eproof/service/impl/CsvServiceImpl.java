package com.hkgov.csb.eproof.service.impl;

import com.hkgov.csb.eproof.entity.AuditLog;
import com.hkgov.csb.eproof.exception.GenericException;
import com.hkgov.csb.eproof.mapper.AuditLogMapper;
import com.hkgov.csb.eproof.service.CsvService;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.MappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.apache.commons.io.input.BOMInputStream;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.hkgov.csb.eproof.exception.ExceptionConstants.FAILED_TO_CONVERT_CSV_EXCEPTION_CODE;
import static com.hkgov.csb.eproof.exception.ExceptionConstants.FAILED_TO_CONVERT_CSV_EXCEPTION_MESSAGE;
import static com.hkgov.csb.eproof.exception.ExceptionConstants.FAILED_TO_READ_CSV_EXCEPTION_CODE;
import static com.hkgov.csb.eproof.exception.ExceptionConstants.FAILED_TO_READ_CSV_EXCEPTION_MESSAGE;

@Service
public class CsvServiceImpl implements CsvService {

    @Override
    public <T> List<T> convertToObject(MultipartFile file, Class<T> classType, MappingStrategy<T> strategy) {
        try (Reader reader = new InputStreamReader(new BOMInputStream(file.getInputStream()))) {
            CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(reader)
                    .withType(classType)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withIgnoreEmptyLine(true)
                    .withMappingStrategy(strategy)
                    .build();
            return csvToBean.parse();
        } catch (Exception e) {
            if (e.getCause() instanceof CsvRequiredFieldEmptyException csvEx) {
                throw GenericException.Builder
                        .create()
                        .setCode(FAILED_TO_READ_CSV_EXCEPTION_CODE)
                        .setMessage(FAILED_TO_READ_CSV_EXCEPTION_MESSAGE)
                        .setCause(csvEx)
                        .setField(csvEx.getLocalizedMessage())
                        .build();
            }
            throw new GenericException(FAILED_TO_READ_CSV_EXCEPTION_CODE, FAILED_TO_READ_CSV_EXCEPTION_MESSAGE, e);
        }
    }

    @Override
    public List<String> getAttendeeEmailFromCsv(MultipartFile file) {
        List<String> attendees = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            csvReader.readNext(); // skip header row
            String[] row;
            while ((row = csvReader.readNext()) != null) {
                attendees.add(row[0]);
            }
        } catch (Exception e) {
            throw new GenericException(FAILED_TO_READ_CSV_EXCEPTION_CODE, FAILED_TO_READ_CSV_EXCEPTION_MESSAGE);
        }
        return attendees;
    }

    @Override
    public byte[] getAuditLogsCsv(List<AuditLog> auditLogs) {
        try (
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(byteArrayOutputStream, StandardCharsets.UTF_8);
                CSVWriter csvWriter = new CSVWriter(outputStreamWriter);
        ) {
            String[] header = {"Date", "Time", "Principal", "ClientIpAddress", "Status", "Action", "Resource"}; // Replace with your desired column names
            csvWriter.writeNext(header);
            StatefulBeanToCsv beanToCsv = new StatefulBeanToCsvBuilder(csvWriter)
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                    .build();
            beanToCsv.write(auditLogs.stream().map(AuditLogMapper.INSTANCE::toCsvDto));
            csvWriter.flush();
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            throw new GenericException(FAILED_TO_CONVERT_CSV_EXCEPTION_CODE, FAILED_TO_CONVERT_CSV_EXCEPTION_MESSAGE, e);
        }
    }
}
