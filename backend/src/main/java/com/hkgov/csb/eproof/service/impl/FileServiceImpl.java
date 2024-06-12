package com.hkgov.csb.eproof.service.impl;

import com.hkgov.csb.eproof.constants.Constants;
import com.hkgov.csb.eproof.dao.FileRepository;
import com.hkgov.csb.eproof.entity.File;
import com.hkgov.csb.eproof.service.FileService;
import com.hkgov.csb.eproof.util.MinioUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;


@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;
    private final MinioUtil minioUtil;
    @Override
    public File uploadFile(String type, String path,String name, InputStream inputStream) throws IOException {

        String saveDestination = "";
        if(StringUtils.equals(path.charAt(path.length() - 1)+"","/")){
            // If the last character is /
            saveDestination = String.format("%s%s", path,name);
        }else{
            saveDestination = String.format("%s/%s", path,name);
        }

        minioUtil.uploadFile(saveDestination,inputStream);

        IOUtils.close(inputStream);

        File file = new File();
        file.setType(type);
        file.setPath(saveDestination);
        file.setName(name);
        file.setStatus(Constants.STATUS_ACTIVE);
        fileRepository.save(file);

        return file;
    }
}




