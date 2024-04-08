package com.hkgov.ceo.pms.service.impl;

import com.hkgov.ceo.pms.dao.AgendaItemHasDocumentRepository;
import com.hkgov.ceo.pms.dao.FileRepository;
import com.hkgov.ceo.pms.dao.MeetingWorkspaceHasPostMeetingDocumentRepository;
import com.hkgov.ceo.pms.dao.MeetingWorkspaceHasPrivateDocumentRepository;
import com.hkgov.ceo.pms.entity.AgendaItem;
import com.hkgov.ceo.pms.entity.AgendaItemHasDocument;
import com.hkgov.ceo.pms.entity.File;
import com.hkgov.ceo.pms.entity.MeetingGroup;
import com.hkgov.ceo.pms.entity.MeetingWorkspace;
import com.hkgov.ceo.pms.entity.MeetingWorkspaceHasPostMeetingDocument;
import com.hkgov.ceo.pms.entity.MeetingWorkspaceHasPrivateDocument;
import com.hkgov.ceo.pms.entity.User;
import com.hkgov.ceo.pms.entity.UserHasMeetingGroup;
import com.hkgov.ceo.pms.entity.enums.Status;
import com.hkgov.ceo.pms.exception.GenericException;
import com.hkgov.ceo.pms.service.AgendaItemService;
import com.hkgov.ceo.pms.service.AuthenticatedInfoService;
import com.hkgov.ceo.pms.service.DocumentService;
import com.hkgov.ceo.pms.service.MeetingGroupService;
import com.hkgov.ceo.pms.service.MeetingWorkspaceService;
import com.hkgov.ceo.pms.service.UserService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.hkgov.ceo.pms.config.Constants.MEETING_PATH;
import static com.hkgov.ceo.pms.config.Constants.SLASH;
import static com.hkgov.ceo.pms.exception.ExceptionConstants.FAILED_TO_READ_TXT_EXCEPTION_CODE;
import static com.hkgov.ceo.pms.exception.ExceptionConstants.FAILED_TO_READ_TXT_EXCEPTION_MESSAGE;
import static com.hkgov.ceo.pms.exception.ExceptionConstants.FILE_NAME_ALREADY_EXIST_EXCEPTION_CODE;
import static com.hkgov.ceo.pms.exception.ExceptionConstants.FILE_NAME_ALREADY_EXIST_EXCEPTION_MESSAGE;
import static com.hkgov.ceo.pms.exception.ExceptionConstants.FILE_NOT_FOUND_EXCEPTION_CODE;
import static com.hkgov.ceo.pms.exception.ExceptionConstants.FILE_NOT_FOUND_EXCEPTION_MESSAGE;
import static com.hkgov.ceo.pms.exception.ExceptionConstants.NO_PERMISSION_TO_DOWNLOAD_EXCEPTION_CODE;
import static com.hkgov.ceo.pms.exception.ExceptionConstants.NO_PERMISSION_TO_DOWNLOAD_EXCEPTION_MESSAGE;

@Service
@Transactional
public class DocumentServiceImpl implements DocumentService {
    private final AgendaItemService agendaItemService;
    private final AgendaItemHasDocumentRepository agendaItemHasDocumentRepository;
    private final MeetingWorkspaceService meetingWorkspaceService;
    private final MeetingWorkspaceHasPostMeetingDocumentRepository meetingWorkspaceHasPostMeetingDocumentRepository;
    private final MeetingWorkspaceHasPrivateDocumentRepository meetingWorkspaceHasPrivateDocumentRepository;
    private final AuthenticatedInfoService authenticatedInfoService;
    private final FileRepository fileRepository;
    private final MeetingGroupService meetingGroupService;
    private final UserService userService;

    public DocumentServiceImpl(AgendaItemService agendaItemService, AgendaItemHasDocumentRepository agendaItemHasDocumentRepository, MeetingWorkspaceService meetingWorkspaceService, MeetingWorkspaceHasPostMeetingDocumentRepository meetingWorkspaceHasPostMeetingDocumentRepository, MeetingWorkspaceHasPrivateDocumentRepository meetingWorkspaceHasPrivateDocumentRepository, AuthenticatedInfoService authenticatedInfoService, FileRepository fileRepository, MeetingGroupService meetingGroupService, UserService userService) {
        this.agendaItemService = agendaItemService;
        this.agendaItemHasDocumentRepository = agendaItemHasDocumentRepository;
        this.meetingWorkspaceService = meetingWorkspaceService;
        this.meetingWorkspaceHasPostMeetingDocumentRepository = meetingWorkspaceHasPostMeetingDocumentRepository;
        this.meetingWorkspaceHasPrivateDocumentRepository = meetingWorkspaceHasPrivateDocumentRepository;
        this.authenticatedInfoService = authenticatedInfoService;
        this.fileRepository = fileRepository;
        this.meetingGroupService = meetingGroupService;
        this.userService = userService;
    }


    @Override
    public String getUploadMeetingDocumentLocation(Long meetingId, Long agendaId, String fileName) {
        return MEETING_PATH + meetingId + "/Agenda Item/" + agendaId + "/" + fileName;
    }

    @Override
    public String getUploadPostMeetingDocumentLocation(Long meetingId, String fileName) {
        return MEETING_PATH + meetingId + "/Post Meeting Documents/" + fileName;
    }

    @Override
    public String getUploadPrivateDocumentLocation(Long meetingId, String fileName, String meetingGroupCode, String path) {
        String subPath = StringUtils.isBlank(path) ? SLASH : path + SLASH;
        return MEETING_PATH + meetingId + "/Private Documents/" + meetingGroupService.getMeetingGroupByCode(meetingGroupCode).getName() + subPath + fileName;
    }

    @Override
    public AgendaItem saveMeetingDocument(Long meetingId, Long agendaId, String fileName, String fileType) {
        User user = authenticatedInfoService.getCurrentUser();
        return createAgendaItemHasDocument(agendaId, fileName, fileType, getUploadMeetingDocumentLocation(meetingId, agendaId, fileName), user);
    }

    @Override
    public MeetingWorkspace savePostMeetingDocument(Long meetingId, String fileName, String fileType) {
        User user = authenticatedInfoService.getCurrentUser();
        return createMeetingWorkspaceHasPostMeetingDocument(meetingId, fileName, fileType, getUploadPostMeetingDocumentLocation(meetingId, fileName), user);
    }

    @Override
    public MeetingWorkspace savePrivateDocument(Long meetingId, String fileName, String fileType, String groupCode, String path) {
        MeetingGroup meetingGroup = meetingGroupService.getMeetingGroupByCode(groupCode);
        return createMeetingWorkspaceHasPrivateDocument(meetingId, fileName, fileType, getUploadPrivateDocumentLocation(meetingId, fileName, groupCode, path), meetingGroup);
    }

    @Override
    public MeetingWorkspace saveCopyPrivateDocument(Long meetingId, String groupCode, String sourcePath, String targetPath) {
        MeetingGroup meetingGroup = meetingGroupService.getMeetingGroupByCode(groupCode);
        File sourceFile = getFileByPath(sourcePath);
        return createMeetingWorkspaceHasPrivateDocument(meetingId, sourceFile.getFileName(), sourceFile.getFileType(), getCopyTargetPath(sourceFile.getFileName(), targetPath), meetingGroup);
    }

    @NotNull
    private static String getCopyTargetPath(String fileName, String path) {
        return path + SLASH + fileName;
    }

    @Override
    public void createPrivateDocumentRootFolder(Long meetingWorkspaceId, String loginId) {
        User user = userService.getUserByLoginId(loginId);
        user.getUserHasMeetingGroups()
                .stream()
                .map(UserHasMeetingGroup::getMeetingGroup)
                .filter(group ->
                        ObjectUtils.isEmpty(fileRepository.findByPath(getUploadPrivateDocumentLocation(meetingWorkspaceId, StringUtils.EMPTY, group.getCode(), StringUtils.EMPTY))))
                .forEach(group ->
                        createMeetingWorkspaceHasPrivateDocument(meetingWorkspaceId, StringUtils.EMPTY, StringUtils.EMPTY, getUploadPrivateDocumentLocation(meetingWorkspaceId, StringUtils.EMPTY, group.getCode(), StringUtils.EMPTY), group));
    }

    @Override
    public AgendaItem removeMeetingDocument(String path) {
        AgendaItemHasDocument agendaItemHasDocument = getAgendaItemDocument(path);
        meetingWorkspaceService.freezeValidation(agendaItemHasDocument.getAgendaItem().getMeetingWorkspace().getMeetingWorkspaceId());
        agendaItemHasDocumentRepository.delete(agendaItemHasDocument);
        return agendaItemHasDocument.getAgendaItem();
    }

    @Override
    public MeetingWorkspace removePrivateDocument(String path) {
        MeetingWorkspaceHasPrivateDocument document = getPrivateDocument(path);
        meetingWorkspaceService.freezeValidation(document.getId().getMeetingWorkspaceId());
        meetingWorkspaceHasPrivateDocumentRepository.delete(document);
        return document.getMeetingWorkspace();
    }

    @Override
    public MeetingWorkspace removePostMeetingDocument(MeetingWorkspace meetingWorkspace, String path) {
        MeetingWorkspaceHasPostMeetingDocument document = getPostMeetingDocument(meetingWorkspace, path);
        meetingWorkspaceService.freezeValidation(meetingWorkspace.getMeetingWorkspaceId());
        meetingWorkspaceHasPostMeetingDocumentRepository.delete(document);
        return meetingWorkspace;
    }

    private AgendaItemHasDocument getAgendaItemDocument(String path) {
        return Optional.ofNullable(agendaItemHasDocumentRepository.findByFilePath(path))
                .orElseThrow(() -> new GenericException(FILE_NOT_FOUND_EXCEPTION_CODE, FILE_NOT_FOUND_EXCEPTION_MESSAGE));
    }

    private MeetingWorkspaceHasPrivateDocument getPrivateDocument(String path) {
        return Optional.ofNullable(meetingWorkspaceHasPrivateDocumentRepository.findByFilePath(path))
                .orElseThrow(() -> new GenericException(FILE_NOT_FOUND_EXCEPTION_CODE, FILE_NOT_FOUND_EXCEPTION_MESSAGE));
    }

    private MeetingWorkspaceHasPostMeetingDocument getPostMeetingDocument(MeetingWorkspace meetingWorkspace, String path) {
        return Optional.ofNullable(meetingWorkspaceHasPostMeetingDocumentRepository.findByFilePath(meetingWorkspace, path))
                .orElseThrow(() -> new GenericException(FILE_NOT_FOUND_EXCEPTION_CODE, FILE_NOT_FOUND_EXCEPTION_MESSAGE));
    }

    @Override
    public void checkMeetingDocumentFileNameDuplication(Long agendaItemId, String fileName) {
        AgendaItem agendaItem = agendaItemService.get(agendaItemId);
        if (agendaItemHasDocumentRepository.findFileNameByAgendaItem(agendaItem).contains(fileName)) {
            throw new GenericException(FILE_NAME_ALREADY_EXIST_EXCEPTION_CODE, FILE_NAME_ALREADY_EXIST_EXCEPTION_MESSAGE);
        }
    }

    @Override
    public void checkPostMeetingDocumentFileNameDuplication(Long meetingWorkspaceId, String fileName) {
        if (meetingWorkspaceHasPostMeetingDocumentRepository.findFileNameByMeetingWorkspace(meetingWorkspaceId).contains(fileName)) {
            throw new GenericException(FILE_NAME_ALREADY_EXIST_EXCEPTION_CODE, FILE_NAME_ALREADY_EXIST_EXCEPTION_MESSAGE);
        }
    }

    @Override
    public void checkPrivateDocumentFileNameDuplication(Long meetingWorkspaceId, String meetingGroupCode, String fileName, String path) {
        if (meetingWorkspaceHasPrivateDocumentRepository.findByMeetingWorkspaceAndGroupCodeAndPath(meetingWorkspaceId, meetingGroupCode, path).contains(fileName)) {
            throw new GenericException(FILE_NAME_ALREADY_EXIST_EXCEPTION_CODE, FILE_NAME_ALREADY_EXIST_EXCEPTION_MESSAGE);
        }
    }

    @Override
    public AgendaItem approveMeetingDocument(Long agendaItemId, String path) {
        AgendaItemHasDocument document = agendaItemHasDocumentRepository.findByAgendaItemIdAndFilePath(agendaItemId, path);
        document.setApprovedBy(authenticatedInfoService.getCurrentUser());
        document.setStatus(Status.APPROVED);
        return document.getAgendaItem();
    }

    @Override
    public AgendaItem rejectMeetingDocument(Long agendaItemId, String path) {
        AgendaItemHasDocument document = agendaItemHasDocumentRepository.findByAgendaItemIdAndFilePath(agendaItemId, path);
        document.setRejectedBy(authenticatedInfoService.getCurrentUser());
        document.setStatus(Status.REJECTED);
        return document.getAgendaItem();
    }

    @Override
    public MeetingWorkspace approvePostMeetingDocument(Long meetingWorkspaceId, String path) {
        MeetingWorkspaceHasPostMeetingDocument document = meetingWorkspaceHasPostMeetingDocumentRepository.findByMeetingWorkspaceIdAndFilePath(meetingWorkspaceId, path);
        document.setApprovedBy(authenticatedInfoService.getCurrentUser());
        document.setStatus(Status.APPROVED);
        return document.getMeetingWorkspace();
    }

    @Override
    public MeetingWorkspace rejectPostMeetingDocument(Long meetingWorkspaceId, String path) {
        MeetingWorkspaceHasPostMeetingDocument document = meetingWorkspaceHasPostMeetingDocumentRepository.findByMeetingWorkspaceIdAndFilePath(meetingWorkspaceId, path);
        document.setRejectedBy(authenticatedInfoService.getCurrentUser());
        document.setStatus(Status.REJECTED);
        return document.getMeetingWorkspace();
    }

    @Override
    public File getFileByPath(String path) {
        return Optional.ofNullable(fileRepository.findByPath(path))
                .orElseThrow(() -> new GenericException(FILE_NOT_FOUND_EXCEPTION_CODE, FILE_NOT_FOUND_EXCEPTION_MESSAGE));
    }

    @Override
    public Set<String> getAttendeeEmailFromTxt(MultipartFile multipart) {
        try {
            String content = new String(multipart.getBytes());
            return Arrays.stream(content.split("\r\n"))
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        } catch (Exception e) {
            throw new GenericException(FAILED_TO_READ_TXT_EXCEPTION_CODE, FAILED_TO_READ_TXT_EXCEPTION_MESSAGE);
        }
    }

    @Override
    public void validateDownloadDocumentPermission(String path) {
        if (!userService.isMeetingWorkspaceMaintenance() && fileRepository.findApprovedOrCorrectMeetingGroupDocument(path, userService.getCurrentUserMeetingGroup()) == null) {
            throw new GenericException(NO_PERMISSION_TO_DOWNLOAD_EXCEPTION_CODE, NO_PERMISSION_TO_DOWNLOAD_EXCEPTION_MESSAGE);
        }
    }

    private AgendaItem createAgendaItemHasDocument(Long agendaId, String fileName, String type, String path, User preparedBy) {
        AgendaItem agendaItem = agendaItemService.get(agendaId);
        File file = create(fileName, type, path);
        agendaItem.addAgendaItemHasDocuments(file, preparedBy);
        return agendaItem;
    }

    private MeetingWorkspace createMeetingWorkspaceHasPostMeetingDocument(Long meetingWorkspaceId, String fileName, String type, String path, User preparedBy) {
        MeetingWorkspace meetingWorkspace = meetingWorkspaceService.getMeetingWorkspaceById(meetingWorkspaceId);
        File file = create(fileName, type, path);
        meetingWorkspace.addMeetingWorkspaceHasPostMeetingDocuments(file, preparedBy);
        return meetingWorkspace;
    }

    private MeetingWorkspace createMeetingWorkspaceHasPrivateDocument(Long meetingWorkspaceId, String fileName, String type, String targetPath, MeetingGroup meetingGroup) {
        checkPrivateDocumentFileNameDuplication(meetingWorkspaceId, meetingGroup.getCode(), fileName, targetPath);
        MeetingWorkspace meetingWorkspace = meetingWorkspaceService.getMeetingWorkspaceById(meetingWorkspaceId);
        File file = create(fileName, type, targetPath);
        meetingWorkspace.addMeetingWorkspaceHasPrivateDocument(file, meetingGroup);
        return meetingWorkspace;
    }

    private File create(String fileName, String type, String path) {
        File file = new File();
        file.setFileName(fileName);
        file.setFileType(type);
        file.setPath(path);
        return file;
    }
}
