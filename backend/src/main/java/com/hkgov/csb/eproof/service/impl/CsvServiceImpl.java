package com.hkgov.csb.eproof.service.impl;

import com.hkgov.csb.eproof.exception.GenericException;
import com.hkgov.csb.eproof.service.CsvService;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.MappingStrategy;
import org.apache.commons.io.input.BOMInputStream;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

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
            throw new GenericException(FAILED_TO_READ_CSV_EXCEPTION_CODE, FAILED_TO_READ_CSV_EXCEPTION_MESSAGE, e);
        }
    }
}
