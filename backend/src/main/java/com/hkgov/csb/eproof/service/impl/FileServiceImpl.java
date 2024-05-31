package com.hkgov.csb.eproof.service.impl;

import com.hkgov.csb.eproof.constants.Constants;
import com.hkgov.csb.eproof.dao.FileRepository;
import com.hkgov.csb.eproof.entity.File;
import com.hkgov.csb.eproof.service.FileService;
import com.hkgov.csb.eproof.util.MinioUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.InputStream;


@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;
    private final MinioUtil minioUtil;
    @Override
    public File uploadFile(String type, String path,String name, InputStream inputStream) {

        String saveDestination = "";
        if(StringUtils.equals(path.charAt(path.length() - 1)+"","/")){
            saveDestination = path+name;
        }else{
            saveDestination = String.format("%s/%s", path,name);
        }

        minioUtil.uploadFile(saveDestination,inputStream);

        File file = new File();
        file.setType(type);
        file.setPath(path);
        file.setStatus(Constants.STATUS_ACTIVE);
        fileRepository.save(file);

        return file;
    }
}




