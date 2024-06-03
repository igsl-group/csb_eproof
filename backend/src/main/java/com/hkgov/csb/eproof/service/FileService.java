package com.hkgov.csb.eproof.service;

import com.hkgov.csb.eproof.dto.CertImportDto;
import com.hkgov.csb.eproof.dto.CertSearchDto;
import com.hkgov.csb.eproof.entity.CertInfo;
import com.hkgov.csb.eproof.entity.File;
import com.hkgov.csb.eproof.entity.enums.CertStage;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;


public interface FileService {

    File uploadFile(String type, String path,String name, InputStream inputStream) throws IOException;
}
