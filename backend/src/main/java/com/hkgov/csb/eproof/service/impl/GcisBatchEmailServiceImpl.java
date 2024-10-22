package com.hkgov.csb.eproof.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hkgov.csb.eproof.constants.Constants;
import com.hkgov.csb.eproof.dao.FileRepository;
import com.hkgov.csb.eproof.dao.GcisBatchEmailRepository;
import com.hkgov.csb.eproof.entity.GcisBatchEmail;
import com.hkgov.csb.eproof.service.GcisBatchEmailService;
import com.hkgov.csb.eproof.util.MinioUtil;
import hk.gov.spica_scopes.common.client.PropertyNames;
import hk.gov.spica_scopes.common.jaxb.ScopesFault;
import hk.gov.spica_scopes.spica.jaxb.batchenq.BatchUploadEnquiryResponse;
import hk.gov.spica_scopes.spica.jaxb.scheenq.ScheduleEnquiryResponse;
import hk.gov.spica_scopes.spica.jaxb.batchupload.BatchUploadResponse;
import hk.gov.spica_scopes.spica.jaxb.schedule.ScheduleResponse;
import hk.gov.spica_scopes.spica.notification.client.restful.NotificationRestfulClient;
import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.UUID;


@Service
public class GcisBatchEmailServiceImpl implements GcisBatchEmailService {

    private final GcisBatchEmailRepository gcisBatchEmailRepository;
    private final ObjectMapper objectMapper;
    private final FileRepository fileRepository;
    private final MinioUtil minioUtil;

    @Value("${gcis-shared-service.batch-email.local-temporary-xml-folder-path}")
    String localTemporaryXmlFolderPath;

    @Value("${gcis-shared-service.batch-email.projectId}")
    String projectId;

    @Value("${gcis-shared-service.batch-email.upload-trial-times}")
    String uploadTrialTimes;

    @Value("${gcis-shared-service.batch-email.schedule-trial-times}")
    String scheduleTrialTimes;

    @Value("${gcis-shared-service.notiSvc.batchUploadEndPointName}")
    String batchUploadEndPointName;

    @Value("${gcis-shared-service.notiSvc.batchUploadEndPointUrl}")
    String batchUploadEndPointUrl;

    @Value("${gcis-shared-service.notiSvc.scheduleEndPointName}")
    String scheduleUploadEndPointName;

    @Value("${gcis-shared-service.notiSvc.scheduleEndPointUrl}")
    String scheduleUploadEndPointUrl;

    @Value("${gcis-shared-service.notiSvc.batchEnquireEndPointName}")
    String batchEnquireEndPointName;

    @Value("${gcis-shared-service.notiSvc.batchEnquireEndPointUrl}")
    String batchEnquireEndPointUrl;

    @Value("${gcis-shared-service.notiSvc.scheduleEnquireEndPointName}")
    String scheduleEnquireEndPointName;

    @Value("${gcis-shared-service.notiSvc.scheduleEnquireEndPointUrl}")
    String scheduleEnquireEndPointUrl;

    @Value("${gcis-shared-service.notiSvc.scheduleEndPointName}")
    String scheduleEndPointName;

    @Value("${gcis-shared-service.notiSvc.scheduleEndPointUrl}")
    String scheduleEndPointUrl;

    @Value("${gcis-shared-service.property.path}")
    private String propertyFilePath;
    // TBC
    @Value("${gcis-shared-service.keystore.path}")
    private String keyStoreFilePath;
    @Value("${gcis-shared-service.keystore.password}")
    private String keyStoreFilePassword;
    @Value("${gcis-shared-service.keystore.alias}")
    private String keyStoreFileAlias;

    Logger logger = LoggerFactory.getLogger(this.getClass());


    public GcisBatchEmailServiceImpl(GcisBatchEmailRepository gcisBatchEmailRepository, @Qualifier("objectMapper") ObjectMapper objectMapper, FileRepository fileRepository, FileRepository fileRepository1, MinioUtil minioUtil) {
        this.gcisBatchEmailRepository = gcisBatchEmailRepository;
        this.objectMapper = objectMapper;
        this.fileRepository = fileRepository1;
        this.minioUtil = minioUtil;
    }

    @Override
    public Boolean uploadBatchXmlToGcis(Long gcisBatchEmailId) throws Exception {
        GcisBatchEmail gcisBatchEmail = gcisBatchEmailRepository.findById(gcisBatchEmailId).orElse(null);
        if(gcisBatchEmail == null){
            return false;
        }
        return this.uploadBatchXmlToGcis(gcisBatchEmail);
    }

    @Override
    public Boolean uploadBatchXmlToGcis(GcisBatchEmail gcisBatchEmail) throws Exception {
        if(gcisBatchEmail == null){
            return false;
        }

        Properties prop = getSSLProperties(batchUploadEndPointName, batchUploadEndPointUrl);

        NotificationRestfulClient notiRestfulClient = new NotificationRestfulClient(prop);
        String batchType = "BATCH";


        com.hkgov.csb.eproof.entity.File file = fileRepository.findById(gcisBatchEmail.getFileId()).orElse(null);

        if(file == null){
            logger.info("WARNING File is null. Please check why the file id is not found in the database. File ID: {}", gcisBatchEmail.getFileId());
            return false;
        }

        String xmlContent = new String(minioUtil.getFileAsByteArray(file.getPath()));
//        String tempXmlFileLocation = createTempXmlFile(gcisBatchEmail.getXml());
        String tempXmlFileLocation = createTempXmlFile(xmlContent);

        Response resp = notiRestfulClient.sendBatchUploadRequest(batchType,tempXmlFileLocation,null);
        boolean uploadSuccess = false;
        if (resp.getStatus() == Response.Status.OK.getStatusCode()) {
            BatchUploadResponse bur = notiRestfulClient.getBatchUploadResponse(resp);
            uploadSuccess = true;
            gcisBatchEmail.setBatchUploadStatus("UPLOADED");
            gcisBatchEmail.setBatchUploadRefNum(bur.getUploadRefNum());
            gcisBatchEmailRepository.save(gcisBatchEmail);

            return uploadSuccess;
        } else{
            ScopesFault faultEntity = notiRestfulClient.getScopesFault(resp);

            System.out.println(notiRestfulClient.getScopesFault(resp).getDescription());
            int currentTrialTimes= 1;

            while(currentTrialTimes < Integer.parseInt(uploadTrialTimes)){
                resp = notiRestfulClient.sendBatchUploadRequest(batchType,tempXmlFileLocation,null);
                if (resp.getStatus() == Response.Status.OK.getStatusCode()) {
                    BatchUploadResponse bur = notiRestfulClient.getBatchUploadResponse(resp);
                    uploadSuccess = true;
                    gcisBatchEmail.setBatchUploadStatus("UPLOADED");
                    gcisBatchEmail.setBatchUploadRefNum(bur.getUploadRefNum());
                    gcisBatchEmailRepository.save(gcisBatchEmail);
                    return uploadSuccess;
                } else{
                    faultEntity = notiRestfulClient.getScopesFault(resp);
                    System.out.println(faultEntity.getDescription());
                }
                currentTrialTimes++;
            }
            // Tried all times but still failed
            if (!uploadSuccess){
                gcisBatchEmail.setBatchUploadStatus("FAILED");
                gcisBatchEmail.setBatchUploadRemark(faultEntity.getDescription());
                gcisBatchEmailRepository.save(gcisBatchEmail);
                sendAlertEmailToInternalTeam("Failed to upload batch email to GCIS. BatchEmailId: " + gcisBatchEmail.getId());
                return uploadSuccess;
            }
        }

        return uploadSuccess;
    }

    private void sendAlertEmailToInternalTeam(String message){
        // TODO: Implement this method
    }

    private String createTempXmlFile(String xml) {
        try{
            File tempXmlDirectory = new File(localTemporaryXmlFolderPath);
            if(!tempXmlDirectory.exists()){
                tempXmlDirectory.mkdirs();
            }

            File tempXmlFile = new File(tempXmlDirectory.getAbsolutePath() + File.separator + UUID.randomUUID()+".xml");
            FileWriter writer = new FileWriter(tempXmlFile);
            writer.write(xml);
            writer.close();

            return tempXmlFile.getAbsolutePath();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ScheduleResponse scheduleBatchEmail(Long gcisBatchEmailId, LocalDateTime scheduleTime) throws Exception {

        GcisBatchEmail gcisBatchEmail = gcisBatchEmailRepository.findById(gcisBatchEmailId).orElse(null);
        if(gcisBatchEmail == null){
            return null;
        }

        return this.scheduleBatchEmail(gcisBatchEmail, scheduleTime);
    }

    @Override
    public ScheduleResponse createSchedule(String startTimestamp, String notiListName, String templateName) throws Exception {

        String scheduleType = "REQUEST";
        String notiListProjId = projectId;
        String templateProjId = projectId;
        Properties prop = getSSLProperties(scheduleUploadEndPointName, scheduleUploadEndPointUrl);

        NotificationRestfulClient notiRestfulClient = new NotificationRestfulClient(prop);
        Response resp = notiRestfulClient.sendScheduleRequest(scheduleType, startTimestamp, null, notiListName,
                notiListProjId, templateName, templateProjId);
        ScheduleResponse buer = new ScheduleResponse();

        if (resp.getStatus() == Response.Status.OK.getStatusCode()) {
            buer = notiRestfulClient.getScheduleResponse(resp);

        } else {
            ScopesFault faultEntity = notiRestfulClient.getScopesFault(resp);
            buer.setResultCd("ERROR"); // You might use a specific code to denote error, if available
            buer.setResultMesg(faultEntity.getDescription());
            System.out.println(faultEntity.getDescription());
        }
        return buer;
    }

    @Override
    public ScheduleResponse scheduleBatchEmail(GcisBatchEmail gcisBatchEmail, LocalDateTime scheduleTime) throws Exception {

        if(gcisBatchEmail == null){
            return null;
        }

        String scheduleType = "REQUEST";
        String startTimestamp = null;
        if (scheduleTime != null) {
            startTimestamp = scheduleTime.format(DateTimeFormatter.ofPattern(Constants.DATE_TIME_PATTERN_3));
        }
        String notiListName = gcisBatchEmail.getGcisNotiListName();
        String templateName = gcisBatchEmail.getGcisTemplateName();
        String notiListProjId = projectId;
        String templateProjId = projectId;
        Properties prop = getSSLProperties(scheduleUploadEndPointName, scheduleUploadEndPointUrl);

        NotificationRestfulClient notiRestfulClient = new NotificationRestfulClient(prop);

        Response resp = notiRestfulClient.sendScheduleRequest(scheduleType, startTimestamp, null, notiListName,
                notiListProjId, templateName, templateProjId);


        boolean scheduleSuccess = false;
        if (resp.getStatus() == Response.Status.OK.getStatusCode()) {
            ScheduleResponse sr = notiRestfulClient.getScheduleResponse(resp);
            gcisBatchEmail.setScheduleEstStartTime(sr.getEstStartTimestamp());
            gcisBatchEmail.setScheduleEstEndTime(sr.getEstEndTimestamp());
            gcisBatchEmailRepository.save(gcisBatchEmail);
            return sr;
        } else {

            ScopesFault scopesFault = notiRestfulClient.getScopesFault(resp);

            System.out.println(notiRestfulClient.getScopesFault(resp).getDescription());
            int currentTrialTimes= 1;

            while(currentTrialTimes < Integer.parseInt(scheduleTrialTimes)){
                resp = notiRestfulClient.sendScheduleRequest(scheduleType, startTimestamp, null, notiListName,
                        notiListProjId, templateName, templateProjId);
                if (resp.getStatus() == Response.Status.OK.getStatusCode()) {
                    scheduleSuccess = true;
                    ScheduleResponse sr = notiRestfulClient.getScheduleResponse(resp);
                    gcisBatchEmail.setScheduleEstStartTime(sr.getEstStartTimestamp());
                    gcisBatchEmail.setScheduleEstEndTime(sr.getEstEndTimestamp());
                    gcisBatchEmailRepository.save(gcisBatchEmail);
                    return sr;
                } else{
                    scopesFault = notiRestfulClient.getScopesFault(resp);
                    System.out.println(scopesFault.getDescription());
                }
                currentTrialTimes++;
            }
            // Tried all times but still failed
            if (!scheduleSuccess){
                gcisBatchEmail.setScheduleJobRemark(scopesFault.getDescription());
                gcisBatchEmailRepository.save(gcisBatchEmail);
                System.out.println(scopesFault.getDescription());
                sendAlertEmailToInternalTeam("Failed to schedule email to GCIS. BatchEmailId: " + gcisBatchEmail.getId());
                return null;
            }
        }

        return null;
    }
    @Override
    public BatchUploadEnquiryResponse enquireUploadStatus(Long gcisBatchEmailId) throws Exception {
        GcisBatchEmail gcisBatchEmail = gcisBatchEmailRepository.findById(gcisBatchEmailId).orElse(null);
        if (gcisBatchEmail == null) {
            return null;
        }

        Properties prop = getSSLProperties(batchEnquireEndPointName, batchEnquireEndPointUrl);
        NotificationRestfulClient notiRestfulClient = new NotificationRestfulClient(prop);

        Response resp = notiRestfulClient.sendBatchUploadEnquiryRequest(gcisBatchEmail.getBatchUploadRefNum());

        if (resp.getStatus() == Response.Status.OK.getStatusCode()) {
            BatchUploadEnquiryResponse buer = notiRestfulClient.getBatchUploadEnquiryResponse(resp);
            return buer;
        } else {
            System.out.println(notiRestfulClient.getScopesFault(resp).getDescription());
            return null;
        }

    }

    @Override
    public BatchUploadResponse batchUpload(MultipartFile file) throws Exception {
        Properties prop = getSSLProperties(batchUploadEndPointName, batchUploadEndPointUrl);
        NotificationRestfulClient notiRestfulClient = new NotificationRestfulClient(prop);
        BatchUploadResponse buer = new BatchUploadResponse();

        String batchType = "BATCH";
        String dataFileLoc = null; //optional
        String xmlContent;
        try (InputStream inputStream = file.getInputStream()) {
            xmlContent = IOUtils.toString(inputStream, "UTF-8");
        }
        String tempXmlFileLocation = createTempXmlFile(xmlContent);
        Response resp = notiRestfulClient.sendBatchUploadRequest(batchType, tempXmlFileLocation, dataFileLoc);

        if (resp.getStatus() == Response.Status.OK.getStatusCode()) {
            buer = notiRestfulClient.getBatchUploadResponse(resp);
        } else {
            // Assuming ScopesFault contains `resultCd` and `description` as a `resultMesg`
            ScopesFault faultEntity = notiRestfulClient.getScopesFault(resp);
            buer.setResultCd("ERROR"); // You might use a specific code to denote error, if available
            buer.setResultMesg(faultEntity.getDescription());
            System.out.println(faultEntity.getDescription());
        }

        return buer;
    }

    @Override
    public BatchUploadEnquiryResponse enquireUploadStatusByBatchUploadRefNum(String batchUploadRefNum) throws Exception {
        Properties prop = getSSLProperties(batchEnquireEndPointName, batchEnquireEndPointUrl);
        NotificationRestfulClient notiRestfulClient = new NotificationRestfulClient(prop);

        Response resp = notiRestfulClient.sendBatchUploadEnquiryRequest(batchUploadRefNum);

        BatchUploadEnquiryResponse buer = new BatchUploadEnquiryResponse();

        if (resp.getStatus() == Response.Status.OK.getStatusCode()) {
            buer = notiRestfulClient.getBatchUploadEnquiryResponse(resp);
        } else {
            // Assuming ScopesFault contains `resultCd` and `description` as a `resultMesg`
            ScopesFault faultEntity = notiRestfulClient.getScopesFault(resp);
            buer.setResultCd("ERROR"); // You might use a specific code to denote error, if available
            buer.setResultMesg(faultEntity.getDescription());
            System.out.println(faultEntity.getDescription());
        }
        return buer;
    }

    @Override
    public ScheduleEnquiryResponse enqSchedule(String jobId) throws Exception {
        Properties prop = getSSLProperties(scheduleEnquireEndPointName, scheduleEnquireEndPointUrl);
        NotificationRestfulClient notiRestfulClient = new NotificationRestfulClient(prop);

        Response resp = notiRestfulClient.sendScheduleEnquiryRequest(jobId);

        ScheduleEnquiryResponse buer = new ScheduleEnquiryResponse();

        if (resp.getStatus() == Response.Status.OK.getStatusCode()) {
            buer = notiRestfulClient.getScheduleEnquiryResponse(resp);
        } else {
            // Assuming ScopesFault contains `resultCd` and `description` as a `resultMesg`
            ScopesFault faultEntity = notiRestfulClient.getScopesFault(resp);
            buer.setResultCd("ERROR"); // You might use a specific code to denote error, if available
            buer.setResultMesg(faultEntity.getDescription());
            System.out.println(faultEntity.getDescription());
        }
        return buer;
    }

    @Override
    public Response enquireScheduleStatus(Long gcisBatchEmailId) throws Exception {

        GcisBatchEmail gcisBatchEmail = gcisBatchEmailRepository.findById(gcisBatchEmailId).orElse(null);
        if (gcisBatchEmail == null) {
            return null;
        }

        Properties prop = getSSLProperties(scheduleEnquireEndPointName, scheduleEnquireEndPointUrl);
        NotificationRestfulClient notiRestfulClient = new NotificationRestfulClient(prop);
        String jobId = gcisBatchEmail.getScheduleJobId();

        return notiRestfulClient.sendScheduleEnquiryRequest(jobId);
    }

    @Override
    public String deleteBatchEmailListFromGcis(Long gcisBatchEmailId) throws Exception {

        GcisBatchEmail gcisBatchEmail = gcisBatchEmailRepository.findById(gcisBatchEmailId).orElse(null);
        if (gcisBatchEmail == null) {
            return "";
        }

        Properties prop = getSSLProperties(batchUploadEndPointName, batchUploadEndPointUrl);
        NotificationRestfulClient notiRestfulClient = new NotificationRestfulClient(prop);
        String batchType = "BATCH";

        com.hkgov.csb.eproof.entity.File file = fileRepository.findById(gcisBatchEmail.getFileId()).orElse(null);

        if(file == null){
            logger.info("WARNING File is null. Please check why the file id is not found in the database. File ID: {}", gcisBatchEmail.getFileId());
            return "";
        }


        String xmlContent = new String(minioUtil.getFileAsByteArray(file.getPath()));
//        String tempXmlFileLocation = createTempXmlFile(gcisBatchEmail.getXml());
        String tempXmlFileLocation = createTempXmlFile(xmlContent);

//        String tempXmlFileLocation = createTempXmlFile(gcisBatchEmail.getXml());
        SAXReader reader = new SAXReader();

        Document doc = reader.read(new File(tempXmlFileLocation));
        Element root = doc.getRootElement();
        Element action = root.element("NOTI_LIST").element("ACTION");
        action.setText("DELETE");

        String tempXmlFileLocation2 = createTempXmlFile(doc.asXML());
        Response resp = notiRestfulClient.sendBatchUploadRequest(batchType,tempXmlFileLocation2,null);

        if (resp.getStatus() == Response.Status.OK.getStatusCode()) {
            BatchUploadResponse bur = notiRestfulClient.getBatchUploadResponse(resp);
            return objectMapper.writeValueAsString(bur);
        } else{
            return null;
        }
    }

    @Override
    public Properties getSSLProperties(String endPointName, String endPointUrl)
            throws Exception {
        String propertyFile = propertyFilePath;
        String keyStoreFile = keyStoreFilePath;
        String keyStorePassword = keyStoreFilePassword;
        String keyStoreAlias = keyStoreFileAlias;

        Properties propAuth = new Properties();
        propAuth.setProperty(PropertyNames.AUTH_TYPE_PROPERTY, PropertyNames.AUTH_SSL_CLIENT_X509_CERTIFICATE);
        propAuth.setProperty(PropertyNames.PROXIMITY_CONFIG_FILE_PROPERTY, propertyFile);
        propAuth.setProperty(PropertyNames.KEY_STORE_FILE_NAME_PROPERTY, keyStoreFile);
        propAuth.setProperty(PropertyNames.KEY_STORE_PASSWORD_PROPERTY, keyStorePassword);
        propAuth.setProperty(PropertyNames.KEY_STORE_TYPE_PROPERTY, PropertyNames.KEY_STORE_TYPE_PKCS12);
        propAuth.setProperty(PropertyNames.KEY_ALIAS_PROPERTY, keyStoreAlias);
        propAuth.setProperty(PropertyNames.KEY_ALIAS_PASSWORD_PROPERTY, keyStorePassword);
        propAuth.setProperty(endPointName, endPointUrl);


        return propAuth;
    }

    @Override
    public Page<GcisBatchEmail> batchEmailList(Pageable pageable, String keyword) {
        return gcisBatchEmailRepository.findPage(pageable,keyword);
    }
}
