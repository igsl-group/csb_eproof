package com.hkgov.ceo.pms.controller;


import com.hkgov.ceo.pms.audit.core.annotation.Audit;
import com.hkgov.ceo.pms.dto.AgendaItemDto;
import com.hkgov.ceo.pms.dto.MeetingWorkspaceDto;
import com.hkgov.ceo.pms.entity.AgendaItem;
import com.hkgov.ceo.pms.entity.MeetingWorkspace;
import com.hkgov.ceo.pms.mapper.AgendaItemMapper;
import com.hkgov.ceo.pms.mapper.MeetingWorkspaceMapper;
import com.hkgov.ceo.pms.service.DocumentService;
import com.hkgov.ceo.pms.service.MeetingWorkspaceService;
import com.hkgov.ceo.pms.service.MinioService;
import com.hkgov.ceo.pms.util.MediaUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static com.hkgov.ceo.pms.config.AuditTrailConstants.AGENDA_ITEM_MEETING_DOCUMENT_WORDING;
import static com.hkgov.ceo.pms.config.AuditTrailConstants.FILE_WORDING;
import static com.hkgov.ceo.pms.config.AuditTrailConstants.POST_MEETING_DOCUMENT_WORDING;
import static com.hkgov.ceo.pms.config.AuditTrailConstants.PRIVATE_DOCUMENT_WORDING;

@RestController
@RequestMapping("/api/v1/document")
public class DocumentController {


    private final MinioService minioService;
    private final DocumentService documentService;
    private final MeetingWorkspaceService meetingWorkspaceService;

    public DocumentController(MinioService minioService, DocumentService documentService, MeetingWorkspaceService meetingWorkspaceService) {
        this.minioService = minioService;
        this.documentService = documentService;
        this.meetingWorkspaceService = meetingWorkspaceService;
    }

    @Transactional
    @Audit(action = "Upload", resourceWording = AGENDA_ITEM_MEETING_DOCUMENT_WORDING, resourceResolverName = "agendaDocumentResourceResolver")
    @PostMapping("/meetingDocument/{meetingWorkspaceId}/{agendaItemId}/upload")
    public AgendaItemDto uploadMeetingDocument(@PathVariable(value = "meetingWorkspaceId") Long meetingWorkspaceId,
                                               @PathVariable(value = "agendaItemId") Long agendaItemId,
                                               @RequestParam MultipartFile multipart) {
        meetingWorkspaceService.freezeValidation(meetingWorkspaceId);
        documentService.checkMeetingDocumentFileNameDuplication(agendaItemId, multipart.getOriginalFilename());
        AgendaItem agendaItem = documentService.saveMeetingDocument(meetingWorkspaceId, agendaItemId, multipart.getOriginalFilename(), MediaUtil.getMediaType(multipart));
        minioService.uploadFile(documentService.getUploadMeetingDocumentLocation(meetingWorkspaceId, agendaItemId, multipart.getOriginalFilename()), multipart);
        return AgendaItemMapper.INSTANCE.sourceToDestinationIgnoreList(agendaItem);
    }

    @Transactional
    @Audit(action = "Upload", resourceWording = POST_MEETING_DOCUMENT_WORDING, resourceResolverName = "postingMeetingDocumentResourceResolver")
    @PostMapping("/postMeetingDocument/{meetingWorkspaceId}/upload")
    public MeetingWorkspaceDto uploadPostMeetingDocument(@PathVariable(value = "meetingWorkspaceId") Long meetingWorkspaceId,
                                                         @RequestParam MultipartFile multipart) {
        meetingWorkspaceService.freezeValidation(meetingWorkspaceId);
        documentService.checkPostMeetingDocumentFileNameDuplication(meetingWorkspaceId, multipart.getOriginalFilename());
        MeetingWorkspace meetingWorkspace = documentService.savePostMeetingDocument(meetingWorkspaceId, multipart.getOriginalFilename(), MediaUtil.getMediaType(multipart));
        minioService.uploadFile(documentService.getUploadPostMeetingDocumentLocation(meetingWorkspaceId, multipart.getOriginalFilename()), multipart);
        return MeetingWorkspaceMapper.INSTANCE.sourceToDestination(meetingWorkspace);
    }

    @Transactional
    @Audit(action = "Upload", resourceWording = PRIVATE_DOCUMENT_WORDING, resourceResolverName = "privateDocumentResourceResolver")
    @PostMapping("/privateDocument/{meetingWorkspaceId}/{meetingGroupCode}/upload")
    public MeetingWorkspaceDto uploadPrivateDocument(@PathVariable(value = "meetingWorkspaceId") Long meetingWorkspaceId,
                                                     @PathVariable(value = "meetingGroupCode") String meetingGroupCode,
                                                     @RequestParam String path,
                                                     @RequestParam MultipartFile multipart) {
        meetingWorkspaceService.freezeValidation(meetingWorkspaceId);
        String realPath = documentService.getUploadPrivateDocumentLocation(meetingWorkspaceId, multipart.getOriginalFilename(), meetingGroupCode, path);
        MeetingWorkspace meetingWorkspace = documentService.savePrivateDocument(meetingWorkspaceId, multipart.getOriginalFilename(), MediaUtil.getMediaType(multipart), meetingGroupCode, path);
        minioService.uploadFile(realPath, multipart);
        return MeetingWorkspaceMapper.INSTANCE.sourceToDestination(meetingWorkspace);
    }

    @Audit(action = "Create", resourceWording = PRIVATE_DOCUMENT_WORDING, resourceResolverName = "privateDocumentResourceResolver")
    @PostMapping("/privateDocument/{meetingWorkspaceId}/{groupCode}/createFolder")
    public MeetingWorkspaceDto meetingWorkspaceHasPrivateMeetingDocumentByMeetingWorkspace(@PathVariable(value = "meetingWorkspaceId") Long meetingWorkspaceId,
                                                                                           @PathVariable(value = "groupCode") String groupCode,
                                                                                           @RequestParam String path) {
        meetingWorkspaceService.freezeValidation(meetingWorkspaceId);
        return MeetingWorkspaceMapper.INSTANCE.sourceToDestination(documentService.savePrivateDocument(meetingWorkspaceId, StringUtils.EMPTY, StringUtils.EMPTY, groupCode, path));
    }

    @Audit(action = "Download", resourceWording = FILE_WORDING, resourceResolverName = "fileResourceResolver")
    @GetMapping("/get")
    public ResponseEntity<Resource> getDocument(@RequestParam String path) {
        documentService.validateDownloadDocumentPermission(path);
        return minioService.getFile(path);
    }

    @Transactional
    @Audit(action = "Delete", resourceWording = AGENDA_ITEM_MEETING_DOCUMENT_WORDING, resourceResolverName = "agendaDocumentResourceResolver")
    @Secured({"MEETING_WORKSPACE_MAINTENANCE"})
    @DeleteMapping("/remove")
    public AgendaItemDto removeMeetingDocument(@RequestParam String path) {
        AgendaItem agendaItem = documentService.removeMeetingDocument(path);
        minioService.removeFile(path);
        return AgendaItemMapper.INSTANCE.sourceToDestinationIgnoreList(agendaItem);
    }

    @Transactional
    @Audit(action = "Delete", resourceWording = PRIVATE_DOCUMENT_WORDING, resourceResolverName = "privateDocumentResourceResolver")
    @DeleteMapping("/privateDocument/remove")
    public MeetingWorkspaceDto removePrivateDocument(@RequestParam String path) {
        MeetingWorkspace meetingWorkspace = documentService.removePrivateDocument(path);
        minioService.removeFile(path);
        return MeetingWorkspaceMapper.INSTANCE.sourceToDestination(meetingWorkspace);
    }

    @Transactional
    @Audit(action = "Delete", resourceWording = POST_MEETING_DOCUMENT_WORDING, resourceResolverName = "postingMeetingDocumentResourceResolver")
    @Secured({"MEETING_WORKSPACE_MAINTENANCE"})
    @DeleteMapping("/postMeetingDocument/{meetingWorkspaceId}/remove")
    public MeetingWorkspaceDto removePostMeetingDocument(@PathVariable(value = "meetingWorkspaceId") Long meetingWorkspaceId,
                                                         @RequestParam String path) {
        MeetingWorkspace meetingWorkspace = meetingWorkspaceService.getMeetingWorkspaceById(meetingWorkspaceId);
        minioService.removeFile(path);
        documentService.removePostMeetingDocument(meetingWorkspace, path);
        return MeetingWorkspaceMapper.INSTANCE.sourceToDestination(meetingWorkspace);
    }

    @Audit(action = "Approve", resourceWording = AGENDA_ITEM_MEETING_DOCUMENT_WORDING, resourceResolverName = "agendaDocumentResourceResolver")
    @Secured({"APPROVE"})
    @PatchMapping("/meetingDocument/{meetingWorkspaceId}/{agendaItemId}/approve")
    public AgendaItemDto approveMeetingDocument(@PathVariable(value = "agendaItemId") Long agendaItemId,
                                                @PathVariable(value = "meetingWorkspaceId") Long meetingWorkspaceId,
                                                @RequestParam String path) {
        meetingWorkspaceService.freezeValidation(meetingWorkspaceId);
        return AgendaItemMapper.INSTANCE.sourceToDestinationIgnoreList(documentService.approveMeetingDocument(agendaItemId, path));
    }

    @Audit(action = "Reject", resourceWording = AGENDA_ITEM_MEETING_DOCUMENT_WORDING, resourceResolverName = "agendaDocumentResourceResolver")
    @Secured({"APPROVE"})
    @PatchMapping("/meetingDocument/{meetingWorkspaceId}/{agendaItemId}/reject")
    public AgendaItemDto rejectMeetingDocument(@PathVariable(value = "agendaItemId") Long agendaItemId,
                                               @PathVariable(value = "meetingWorkspaceId") Long meetingWorkspaceId,
                                               @RequestParam String path) {
        meetingWorkspaceService.freezeValidation(meetingWorkspaceId);
        return AgendaItemMapper.INSTANCE.sourceToDestinationIgnoreList(documentService.rejectMeetingDocument(agendaItemId, path));
    }

    @Audit(action = "Approve", resourceWording = POST_MEETING_DOCUMENT_WORDING, resourceResolverName = "postingMeetingDocumentResourceResolver")
    @Secured({"APPROVE"})
    @PatchMapping("/postMeetingDocument/{meetingWorkspaceId}/approve")
    public MeetingWorkspaceDto approvePostMeetingDocument(@PathVariable(value = "meetingWorkspaceId") Long meetingWorkspaceId,
                                                          @RequestParam String path) {
        meetingWorkspaceService.freezeValidation(meetingWorkspaceId);
        return MeetingWorkspaceMapper.INSTANCE.sourceToDestination(documentService.approvePostMeetingDocument(meetingWorkspaceId, path));
    }

    @Audit(action = "Reject", resourceWording = POST_MEETING_DOCUMENT_WORDING, resourceResolverName = "postingMeetingDocumentResourceResolver")
    @Secured({"APPROVE"})
    @PatchMapping("/postMeetingDocument/{meetingWorkspaceId}/reject")
    public MeetingWorkspaceDto rejectPostMeetingDocument(@PathVariable(value = "meetingWorkspaceId") Long meetingWorkspaceId,
                                                         @RequestParam String path) {
        meetingWorkspaceService.freezeValidation(meetingWorkspaceId);
        return MeetingWorkspaceMapper.INSTANCE.sourceToDestination(documentService.rejectPostMeetingDocument(meetingWorkspaceId, path));
    }

    @Transactional
    @Audit(action = "Copy", resourceWording = PRIVATE_DOCUMENT_WORDING, resourceResolverName = "privateDocumentResourceResolver")
    @PostMapping("/privateDocument/{meetingWorkspaceId}/{meetingGroupCode}/copyDocument")
    public MeetingWorkspaceDto copyPrivateDocument(@PathVariable(value = "meetingWorkspaceId") Long meetingWorkspaceId,
                                                   @PathVariable(value = "meetingGroupCode") String meetingGroupCode,
                                                   @RequestParam String sourcePath,
                                                   @RequestParam String targetPath) {
        meetingWorkspaceService.freezeValidation(meetingWorkspaceId);
        MeetingWorkspace meetingWorkspace = documentService.saveCopyPrivateDocument(meetingWorkspaceId, meetingGroupCode, sourcePath, targetPath);
        minioService.copyFile(sourcePath, targetPath);
        return MeetingWorkspaceMapper.INSTANCE.sourceToDestination(meetingWorkspace);
    }
}
