package com.hkgov.csb.eproof.service;


import com.opencsv.bean.MappingStrategy;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CsvService {
    <T> List<T> convertToObject(MultipartFile file, Class<T> classType, MappingStrategy<T> strategy);
}
