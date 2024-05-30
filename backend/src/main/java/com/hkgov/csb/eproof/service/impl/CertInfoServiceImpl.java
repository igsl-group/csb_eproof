package com.hkgov.csb.eproof.service.impl;

import com.hkgov.csb.eproof.constants.Constants;
import com.hkgov.csb.eproof.constants.enums.ResultCode;
import com.hkgov.csb.eproof.dao.CertInfoRepository;
import com.hkgov.csb.eproof.dao.ExamProfileRepository;
import com.hkgov.csb.eproof.dto.CertImportDto;
import com.hkgov.csb.eproof.dto.CertSearchDto;
import com.hkgov.csb.eproof.entity.CertInfo;
import com.hkgov.csb.eproof.entity.enums.CertStage;
import com.hkgov.csb.eproof.entity.enums.CertStatus;
import com.hkgov.csb.eproof.exception.ServiceException;
import com.hkgov.csb.eproof.mapper.CertInfoMapper;
import com.hkgov.csb.eproof.service.CertInfoService;
import com.hkgov.csb.eproof.util.CodeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
* @author David
* @description 针对表【cert_info】的数据库操作Service实现
* @createDate 2024-05-10 17:47:40
*/
@Service
@RequiredArgsConstructor
public class CertInfoServiceImpl implements CertInfoService {
    private final CertInfoRepository certInfoRepository;
    private final ExamProfileRepository examProfileRepository;

    @Override
    public Page<CertInfo> search(CertSearchDto request, List<String> certStageList, List<String> certStatusList, Pageable pageable) {
        return certInfoRepository.caseSearch(request,certStageList,certStatusList,pageable);
    }

    @Override
    public Boolean batchImport(String examProfileSerialNo,LocalDate examDate, List<CertImportDto> csvData) {
        List<CertInfo> certInfos = checkScv(examProfileSerialNo,examDate,csvData);
        return certInfoRepository.saveAll(certInfos).size() == csvData.size();
    }


    @Override
    public Boolean dispatch(String examProfileSerialNo, CertStage currentStage) {
        if(!currentStage.equals(CertStage.IMPORTED) && !currentStage.equals(CertStage.GENERATED)
                && !currentStage.equals(CertStage.SIGN_ISSUE) && !currentStage.equals(CertStage.NOTIFY)){
            throw new ServiceException(ResultCode.STAGE_ERROR);
        }
        List<CertInfo> list = certInfoRepository.getinfoByNoAndStatus(examProfileSerialNo,currentStage);
        for(CertInfo certInfo : list){
            switch (certInfo.getCertStage()) {
                case IMPORTED -> {
                    certInfo.setCertStage(CertStage.GENERATED);
                    break;
                }
                case GENERATED -> {
                    certInfo.setCertStage(CertStage.SIGN_ISSUE);
                    break;
                }
                case SIGN_ISSUE -> {
                    certInfo.setCertStage(CertStage.NOTIFY);
                    break;
                }
                case NOTIFY -> {
                    certInfo.setCertStage(CertStage.COMPLETED);
                    break;
                }
                default ->{
                    break;
                }
            }
        }
        certInfoRepository.saveAll(list).size();
        return true;
    }

    public List<CertInfo> checkScv(String examProfileSerialNo, LocalDate date,List<CertImportDto> csvData){
        Set<String> hkids = new HashSet<>();
        Set<String> passports = new HashSet<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.EXAM_DATE);
        List<CertImportDto> certInfos = CertInfoMapper.INSTANCE.sourceToDestinationList(certInfoRepository.getinfoByNoList(examProfileSerialNo));
        CodeUtil codeUtil = new CodeUtil();
        int count = certInfos.size();
        List<CertInfo> list = new ArrayList<>();
        certInfos.addAll(csvData);
        for(int i = 0; i < certInfos.size(); ++i){
            CertImportDto csv = certInfos.get(i);
            if(i < count ){
                hkids.add(csv.getHkid());
                passports.add(csv.getPassportNo());
            }else{
                LocalDate examDate;
                int row = i+1-count;
                try{
                    examDate = LocalDate.parse(csv.getExamDate(), formatter);
                }catch (Exception e){
                    throw new ServiceException(ResultCode.CSV_EXAM_DATE,row);
                }
                if(!examDate.equals(date)){
                    throw new ServiceException(ResultCode.CSV_EXAM_DATE,row);
                }
                if(csv.getHkid().contains("(") && csv.getHkid().contains("("))
                    csv.setHkid(csv.getHkid().replaceAll("\\)","").replaceAll("\\(",""));
                if(csv.getHkid().isEmpty() && csv.getPassportNo().isEmpty()){
                    throw new ServiceException(ResultCode.HKID_PANNPORT_ABSENT,row);
                }
                if(!csv.getHkid().isEmpty() && !hkids.add(csv.getHkid())){
                    throw new ServiceException(ResultCode.HKID_EXITS,row);
                }
                if(!csv.getPassportNo().isEmpty() && !passports.add(csv.getPassportNo())){
                    throw new ServiceException(ResultCode.PASSPORT_EXITS,row);
                }
                if(!csv.getLetterType().isEmpty() && (!csv.getLetterType().equals("F") && !csv.getLetterType().equals("P"))){
                    throw new ServiceException(ResultCode.CSV_LETTER_TYPE,row);
                }
                if(!codeUtil.validEmai(csv.getEmail())){
                    throw new ServiceException(ResultCode.CSV_EMAIL_ERROR,row);
                }
                //组装batchinfo数据
                CertInfo certInfo = new CertInfo();
                certInfo.setExamProfileSerialNo(examProfileSerialNo);
                certInfo.setHkid(csv.getHkid());
                certInfo.setPassportNo(csv.getPassportNo());
                certInfo.setExamDate(examDate);
                certInfo.setName(csv.getName());
                certInfo.setEmail(csv.getEmail());
                certInfo.setUeGrade(csv.getUeGrade());
                certInfo.setUcGrade(csv.getUcGrade());
                certInfo.setAtGrade(csv.getAtGrade());
                certInfo.setBlnstGrade(csv.getBlnstGrade());
                certInfo.setLetterType(csv.getLetterType());
                certInfo.setCertStatus(CertStatus.PENDING);
                certInfo.setCertStage(CertStage.IMPORTED);
                certInfo.setOnHold(false);
                list.add(certInfo);
            }
        }
        return list;
    }
}
