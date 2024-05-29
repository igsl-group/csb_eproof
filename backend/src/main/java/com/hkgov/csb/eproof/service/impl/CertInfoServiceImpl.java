package com.hkgov.csb.eproof.service.impl;

import com.hkgov.csb.eproof.dao.CertInfoRepository;
import com.hkgov.csb.eproof.dao.ExamProfileRepository;
import com.hkgov.csb.eproof.dto.CertImportDto;
import com.hkgov.csb.eproof.dto.CertSearchDto;
import com.hkgov.csb.eproof.entity.CertInfo;
import com.hkgov.csb.eproof.entity.ExamProfile;
import com.hkgov.csb.eproof.entity.enums.CertStage;
import com.hkgov.csb.eproof.entity.enums.CertStatus;
import com.hkgov.csb.eproof.exception.ServiceException;
import com.hkgov.csb.eproof.service.CertInfoService;
import com.hkgov.csb.eproof.util.CodeUtil;
import com.hkgov.csb.eproof.util.ResultCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
        ExamProfile examProfile = examProfileRepository.getinfoByNo(examProfileSerialNo);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        int count = csvData.size();
        List<CertInfo> certInfos = certInfoRepository.getinfoByNoList(examProfileSerialNo);
        CodeUtil codeUtil = new CodeUtil();
        List<CertInfo> list = new ArrayList<>();
        for(int i = 0; i < csvData.size(); ++i){
            CertImportDto csv = csvData.get(i);
            LocalDate examDate;
            try{
                examDate = LocalDate.parse(csv.getExamDate(), formatter);
            }catch (Exception e){
                throw new ServiceException(ResultCode.CSV_EXAM_DATE,i+1);
            }

            if(!examDate.equals(date)){
                throw new ServiceException(ResultCode.CSV_EXAM_DATE,i+1);
            }
            if(csv.getHkid().contains("(") && csv.getHkid().contains("("))
                csv.setHkid(csv.getHkid().replaceAll("\\)","").replaceAll("\\(",""));
            if(csv.getHkid().isEmpty() && csv.getPassport().isEmpty()){
                throw new ServiceException(ResultCode.HKID_PANNPORT_ABSENT,i+1);
            }
            if(!csv.getHkid().isEmpty() && (!hkids.add(csv.getHkid()) || certInfos.stream().map(CertInfo::getHkid).anyMatch(s -> s.equals(csv.getHkid())))){
                throw new ServiceException(ResultCode.HKID_EXITS,i+1);
            }
            if(!csv.getPassport().isEmpty() && (!passports.add(csv.getPassport()) || certInfos.stream().map(CertInfo::getPassportNo).anyMatch(s -> s.equals(csv.getHkid())))){
                throw new ServiceException(ResultCode.PASSPORT_EXITS,i+1);
            }
            if(!csv.getLetterType().isEmpty() && (!csv.getLetterType().equals("F") && !csv.getLetterType().equals("P"))){
                throw new ServiceException(ResultCode.CSV_LETTER_TYPE,i+1);
            }

            if(!codeUtil.validEmai(csv.getEmail())){
                throw new ServiceException(ResultCode.CSV_EMAIL_ERROR,i+1);
            }
            if(i< count){
                //组装batchinfo数据
                CertInfo certInfo = new CertInfo();
                certInfo.setExamProfile(examProfile);
                certInfo.setHkid(csv.getHkid());
                certInfo.setPassportNo(csv.getPassport());
                certInfo.setExamDate(examDate);
                certInfo.setName(csv.getName());
                certInfo.setEmail(csv.getEmail());
                certInfo.setUeGrade(csv.getUeGrade());
                certInfo.setUcGrade(csv.getUcGrade());
                certInfo.setAtGrade(csv.getAtGrade());
                certInfo.setBlnstGrade(csv.getBlGrade());
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
