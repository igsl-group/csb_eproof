package com.hkgov.csb.eproof.service;

import com.hkgov.csb.eproof.entity.AgendaItem;
import com.hkgov.csb.eproof.entity.File;
import com.hkgov.csb.eproof.entity.MeetingWorkspace;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public interface DocumentService {
    String getUploadMeetingDocumentLocation(Long meetingId, Long agendaId, String fileName);

    String getUploadPostMeetingDocumentLocation(Long meetingId, String fileName);

    String getUploadPrivateDocumentLocation(Long meetingId, String fileName, String meetingGroupCode, String path);

    AgendaItem saveMeetingDocument(Long meetingId, Long agendaId, String fileName, String fileType);

    MeetingWorkspace savePostMeetingDocument(Long meetingId, String fileName, String fileType);

    MeetingWorkspace savePrivateDocument(Long meetingId, String departmentCode, String fileName, String fileType, String path);

    MeetingWorkspace saveCopyPrivateDocument(Long meetingId, String groupCode, String sourcePath, String targetPath);

    void createPrivateDocumentRootFolder(Long meetingWorkspaceId, String loginId);

    AgendaItem removeMeetingDocument(String path);

    MeetingWorkspace removePrivateDocument(String path);

    MeetingWorkspace removePostMeetingDocument(MeetingWorkspace meetingWorkspace, String path);

    void checkMeetingDocumentFileNameDuplication(Long agendaItemId, String fileName);

    void checkPostMeetingDocumentFileNameDuplication(Long meetingWorkspaceId, String fileName);

    void checkPrivateDocumentFileNameDuplication(Long meetingWorkspaceId, String departmentCode, String fileName, String path);

    AgendaItem approveMeetingDocument(Long agendaItemId, String path);

    AgendaItem rejectMeetingDocument(Long agendaItemId, String path);

    MeetingWorkspace approvePostMeetingDocument(Long meetingWorkspaceId, String path);

    MeetingWorkspace rejectPostMeetingDocument(Long meetingWorkspaceId, String path);

    File getFileByPath(String path);

    Set<String> getAttendeeEmailFromTxt(MultipartFile multipart);

    void validateDownloadDocumentPermission(String path);
}
